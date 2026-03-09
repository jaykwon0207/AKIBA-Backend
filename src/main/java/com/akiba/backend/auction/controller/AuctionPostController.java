// ========================================================================
// 파일 경로: com/akiba/backend/auction/controller/AuctionPostController.java
// 설명: 경매 관련 REST API 엔드포인트 (14개)
//
// [경매만의 특수 API]
// - 입찰하기 (POST /api/auction/posts/{postId}/bids)
// - 즉시구매 (POST /api/auction/posts/{postId}/buy-now)
// - 입찰 내역 조회 (GET /api/auction/posts/{postId}/bids)
// - 내 입찰 목록 (GET /api/auction/my/bids)
// - 내 경매 목록 (GET /api/auction/my/posts)
// - 낙찰 성공 목록 (GET /api/auction/my/won)
// - 마감 임박 경매 (GET /api/auction/posts/ending-soon)
// ========================================================================
package com.akiba.backend.auction.controller;

import com.akiba.backend.auction.dto.request.*;
import com.akiba.backend.auction.dto.response.*;
import com.akiba.backend.auction.service.AuctionPostService;
import com.akiba.backend.market.dto.response.PopularKeywordResponse;
import com.akiba.backend.search.service.SearchKeywordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
public class AuctionPostController {

    private final AuctionPostService auctionPostService;
    private final SearchKeywordService searchKeywordService;

    // =========================================================================
    // 1. 경매 글 작성
    // POST /api/auction/posts
    // =========================================================================
    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AuctionPostCreateRequest request) {

        Long postId = auctionPostService.createPost(requireUserId(userId), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("postId", postId, "message", "경매가 등록되었습니다."));
    }

    // =========================================================================
    // 2. 경매 목록 조회
    // GET /api/auction/posts?status=ACTIVE&sort=latest&page=0&size=20
    // =========================================================================
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPostList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(auctionPostService.getPostList(status, sort, page, size));
    }

    // =========================================================================
    // 3. 경매 상세 조회
    // GET /api/auction/posts/{postId}
    // =========================================================================
    @GetMapping("/posts/{postId}")
    public ResponseEntity<AuctionPostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(auctionPostService.getPostDetail(postId, userId));
    }

    // =========================================================================
    // 4. 경매 글 수정 (입찰 없을 때만)
    // PUT /api/auction/posts/{postId}
    // =========================================================================
    @PutMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AuctionPostUpdateRequest request) {

        auctionPostService.updatePost(postId, requireUserId(userId), request);
        return ResponseEntity.ok(Map.of("message", "경매가 수정되었습니다."));
    }

    // =========================================================================
    // 5. 경매 글 삭제 (입찰 없을 때만)
    // DELETE /api/auction/posts/{postId}
    // =========================================================================
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {

        auctionPostService.deletePost(postId, requireUserId(userId));
        return ResponseEntity.ok(Map.of("message", "경매가 삭제되었습니다."));
    }

    // =========================================================================
    // 6. 입찰하기
    // POST /api/auction/posts/{postId}/bids
    // =========================================================================
    @PostMapping({"/posts/{postId}/bids", "/posts/{postId}/bid"})
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BidRequest request) {

        BidResponse response = auctionPostService.placeBid(postId, requireUserId(userId), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================================================================
    // 7. 즉시구매
    // POST /api/auction/posts/{postId}/buy-now
    // =========================================================================
    @PostMapping("/posts/{postId}/buy-now")
    public ResponseEntity<BidResponse> buyNow(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {

        BidResponse response = auctionPostService.buyNow(postId, requireUserId(userId));
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 8. 입찰 내역 조회 — GET /api/auction/posts/{postId}/bids
    @GetMapping("/posts/{postId}/bids")
    public ResponseEntity<BidHistoryListResponse> getBidHistory(@PathVariable Long postId) {
        return ResponseEntity.ok(auctionPostService.getBidHistoryList(postId));
    }

    // =========================================================================
    // 9. 내 입찰 목록 조회
    // GET /api/auction/my/bids?page=0&size=20
    // =========================================================================
    @GetMapping("/my/bids")
    public ResponseEntity<Map<String, Object>> getMyBids(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(auctionPostService.getMyBids(requireUserId(userId), page, size));
    }

    // =========================================================================
    // 10. 내 경매 목록 조회
    // GET /api/auction/my/posts?page=0&size=20
    // =========================================================================
    @GetMapping("/my/posts")
    public ResponseEntity<Map<String, Object>> getMyPosts(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(auctionPostService.getMyPosts(requireUserId(userId), page, size));
    }

    // =========================================================================
    // 11. 낙찰 성공 목록 조회
    // GET /api/auction/my/won?page=0&size=20
    // =========================================================================
    @GetMapping("/my/won")
    public ResponseEntity<Map<String, Object>> getMyWonPosts(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(auctionPostService.getMyWonPosts(requireUserId(userId), page, size));
    }

    // =========================================================================
    // 12. 인기 경매 조회
    // GET /api/auction/posts/popular?limit=10
    // =========================================================================
    @GetMapping("/posts/popular")
    public ResponseEntity<Map<String, Object>> getPopularAuctions(
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(Map.of("posts", auctionPostService.getPopularAuctions(limit)));
    }

    // =========================================================================
    // 13. 마감 임박 경매 조회 ("곧 끝나는 경매!")
    // GET /api/auction/posts/ending-soon?limit=10
    // =========================================================================
    @GetMapping("/posts/ending-soon")
    public ResponseEntity<Map<String, Object>> getEndingSoonAuctions(
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(Map.of("posts", auctionPostService.getEndingSoonAuctions(limit)));
    }

    // =========================================================================
    // 14. 인기 검색어 조회 (경매용)
    // GET /api/auction/search/popular
    // =========================================================================
    @GetMapping("/search/popular")
    public ResponseEntity<PopularKeywordResponse> getPopularKeywords() {
        return ResponseEntity.ok(searchKeywordService.getPopularKeywords(10));
    }

    // =========================================================================
    // 15. 추천 검색 태그 (경매용)
    // GET /api/auction/tags/recommended
    // =========================================================================
    @GetMapping("/tags/recommended")
    public ResponseEntity<Map<String, Object>> getRecommendedTags() {
        // TODO: 인기 태그 기반 추천 로직 구현
        return ResponseEntity.ok(Map.of("tags", List.of()));
    }

    private Long requireUserId(Long principalUserId) {
        if (principalUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        return principalUserId;
    }
}
