package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.BoardCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardCommentLikeRepository extends JpaRepository<BoardCommentLike, Long> {

    Optional<BoardCommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    List<BoardCommentLike> findByCommentIdIn(List<Long> commentIds);

    long countByCommentId(Long commentId);

    void deleteByCommentId(Long commentId);

    void deleteByCommentIdIn(List<Long> commentIds);
}
