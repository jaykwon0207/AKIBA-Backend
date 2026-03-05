// ========================================================================
// 파일 경로: com/akiba/backend/wanted/controller/WantedPostController.java
// 설명: 구해요 관련 REST API 엔드포인트 (6개)
//
// [market과의 차이점]
// - URL이 /api/wanted/... 로 분리
// - 구해요는 희망 가격 범위(min~max)가 있음
// - 상품 상태/카테고리 필터가 없음 (더 단순)
// ========================================================================
package com.akiba.backend.wanted.controller;

import com.akiba.backend.wanted.dto.request.*;
import com.akiba.backend.wanted.dto.response.*;
import com.akiba.backend.wanted.service.WantedPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/wanted")
@RequiredArgsConstructor
public class WantedPostController {

    private final WantedPostService wantedPostService;

    // =========================================================================
    // 1. 구해요 글 작성
    // POST /api/wanted/posts
    // =========================================================================
    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WantedPostCreateRequest request) {

        Long postId = wantedPostService.createPost(requireUserId(userId), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("postId", postId, "message", "구해요 게시글이 등록되었습니다."));
    }

    // =========================================================================
    // 2. 구해요 목록 조회
    // GET /api/wanted/posts?sort=latest&page=0&size=20
    // =========================================================================
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPostList(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(wantedPostService.getPostList(sort, page, size));
    }

    // =========================================================================
    // 3. 구해요 상세 조회
    // GET /api/wanted/posts/{postId}
    // =========================================================================
    @GetMapping("/posts/{postId}")
    public ResponseEntity<WantedPostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(wantedPostService.getPostDetail(postId, userId));
    }

    // =========================================================================
    // 4. 구해요 글 수정
    // PUT /api/wanted/posts/{postId}
    // =========================================================================
    @PutMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WantedPostUpdateRequest request) {

        wantedPostService.updatePost(postId, requireUserId(userId), request);
        return ResponseEntity.ok(Map.of("message", "구해요 게시글이 수정되었습니다."));
    }

    // =========================================================================
    // 5. 구해요 글 삭제
    // DELETE /api/wanted/posts/{postId}
    // =========================================================================
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {

        wantedPostService.deletePost(postId, requireUserId(userId));
        return ResponseEntity.ok(Map.of("message", "구해요 게시글이 삭제되었습니다."));
    }

    private Long requireUserId(Long principalUserId) {
        if (principalUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        return principalUserId;
    }
}
