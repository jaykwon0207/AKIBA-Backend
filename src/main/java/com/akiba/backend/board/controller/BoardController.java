package com.akiba.backend.board.controller;

import com.akiba.backend.board.domain.BoardCode;
import com.akiba.backend.board.dto.BoardDtos;
import com.akiba.backend.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public List<BoardDtos.BoardSummaryResponse> listBoards() {
        return boardService.listBoards();
    }

    @GetMapping("/{boardCode}/posts")
    public List<BoardDtos.PostSummaryResponse> listPosts(@PathVariable BoardCode boardCode) {
        return boardService.listPosts(boardCode);
    }

    @GetMapping("/popular/posts")
    public List<BoardDtos.PostSummaryResponse> listPopularPosts() {
        return boardService.listPopularPosts();
    }

    @GetMapping("/search")
    public List<BoardDtos.PostSummaryResponse> searchPosts(@RequestParam String keyword) {
        return boardService.searchPosts(keyword);
    }

    @GetMapping("/hashtags/{hashtag}/posts")
    public List<BoardDtos.PostSummaryResponse> listPostsByHashtag(@PathVariable String hashtag) {
        return boardService.listPostsByHashtag(hashtag);
    }

    @PostMapping("/{boardCode}/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardDtos.PostDetailResponse createPost(
            @PathVariable BoardCode boardCode,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid BoardDtos.CreatePostRequest request
    ) {
        requireBearerToken(authorization);
        return boardService.createPost(boardCode, request);
    }

    @GetMapping("/{boardCode}/posts/{postId}")
    public BoardDtos.PostDetailResponse getPost(
            @PathVariable BoardCode boardCode,
            @PathVariable Long postId
    ) {
        return boardService.getPost(boardCode, postId);
    }

    @PutMapping("/{boardCode}/posts/{postId}")
    public BoardDtos.PostDetailResponse updatePost(
            @PathVariable BoardCode boardCode,
            @PathVariable Long postId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid BoardDtos.UpdatePostRequest request
    ) {
        requireBearerToken(authorization);
        return boardService.updatePost(boardCode, postId, request);
    }

    @DeleteMapping("/{boardCode}/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable BoardCode boardCode,
            @PathVariable Long postId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam Long userId
    ) {
        requireBearerToken(authorization);
        boardService.deletePost(boardCode, postId, userId);
    }

    @PostMapping("/{boardCode}/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardDtos.CommentResponse createComment(
            @PathVariable BoardCode boardCode,
            @PathVariable Long postId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid BoardDtos.CreateCommentRequest request
    ) {
        requireBearerToken(authorization);
        return boardService.createComment(boardCode, postId, request);
    }

    @GetMapping("/{boardCode}/posts/{postId}/comments")
    public List<BoardDtos.CommentResponse> listComments(
            @PathVariable BoardCode boardCode,
            @PathVariable Long postId
    ) {
        return boardService.listComments(boardCode, postId);
    }

    @DeleteMapping("/{boardCode}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable BoardCode boardCode,
            @PathVariable Long commentId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam Long userId
    ) {
        requireBearerToken(authorization);
        boardService.deleteComment(boardCode, commentId, userId);
    }

    @PostMapping("/{boardCode}/posts/{postId}/like")
    public BoardDtos.PostDetailResponse toggleLike(
            @PathVariable BoardCode boardCode,
            @PathVariable Long postId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid BoardDtos.ToggleLikeRequest request
    ) {
        requireBearerToken(authorization);
        return boardService.toggleLike(boardCode, postId, request);
    }

    @PostMapping("/{boardCode}/comments/{commentId}/like")
    public BoardDtos.CommentLikeToggleResponse toggleCommentLike(
            @PathVariable BoardCode boardCode,
            @PathVariable Long commentId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid BoardDtos.ToggleCommentLikeRequest request
    ) {
        requireBearerToken(authorization);
        return boardService.toggleCommentLike(boardCode, commentId, request);
    }

    @PostMapping("/{boardCode}/posts/{postId}/votes")
    public BoardDtos.PostDetailResponse vote(
            @PathVariable BoardCode boardCode,
            @PathVariable Long postId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid BoardDtos.VoteRequest request
    ) {
        requireBearerToken(authorization);
        return boardService.vote(boardCode, postId, request);
    }

    private void requireBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")
                || authorization.substring("Bearer ".length()).trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효한 액세스 토큰이 필요합니다.");
        }
    }
}
