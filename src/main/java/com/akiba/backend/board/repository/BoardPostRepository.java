package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    List<BoardPost> findByBoardIdOrderByCreatedAtDesc(Long boardId);

    List<BoardPost> findTop10ByOrderByLikeCountDesc();
}
