package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    List<BoardPost> findByBoardIdOrderByCreatedAtDesc(Long boardId);

    List<BoardPost> findTop10ByOrderByLikeCountDescCreatedAtDesc();

    Optional<BoardPost> findByPostIdAndBoardId(Long postId, Long boardId);

    List<BoardPost> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String titleKeyword,
            String contentKeyword
    );
}
