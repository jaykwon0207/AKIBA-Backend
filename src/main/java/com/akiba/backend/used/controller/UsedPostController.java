package com.akiba.backend.used.controller;

import com.akiba.backend.used.dto.request.UsedPostCreateRequest;
import com.akiba.backend.used.dto.request.UsedPostUpdateRequest;
import com.akiba.backend.used.dto.response.UsedPostDetailResponse;
import com.akiba.backend.used.service.UsedPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsedPostController {

    private final UsedPostService usedPostService;

    @PostMapping("/used/posts")
    public ResponseEntity<Map<String, Object>> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UsedPostCreateRequest request) {

        Long postId = usedPostService.createPost(requireUserId(userId), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("postId", postId, "message", "게시글이 등록되었습니다."));
    }

    @GetMapping("/used/posts")
    public ResponseEntity<Map<String, Object>> getPostList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(usedPostService.getPostList(categoryId, status, sort, page, size));
    }

    @GetMapping("/used/posts/{postId}")
    public ResponseEntity<UsedPostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(usedPostService.getPostDetail(postId, userId));
    }

    @PutMapping("/used/posts/{postId}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UsedPostUpdateRequest request) {

        usedPostService.updatePost(postId, requireUserId(userId), request);
        return ResponseEntity.ok(Map.of("message", "게시글이 수정되었습니다."));
    }

    @DeleteMapping("/used/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {

        usedPostService.deletePost(postId, requireUserId(userId));
        return ResponseEntity.ok(Map.of("message", "게시글이 삭제되었습니다."));
    }

    @GetMapping("/used/posts/popular")
    public ResponseEntity<Map<String, Object>> getPopularPosts(
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(Map.of("posts", usedPostService.getPopularPosts(limit)));
    }

    private Long requireUserId(Long principalUserId) {
        if (principalUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        return principalUserId;
    }
}
