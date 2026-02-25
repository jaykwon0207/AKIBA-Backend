package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    List<BoardComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    long countByPostId(Long postId);

    void deleteByPostId(Long postId);
}
