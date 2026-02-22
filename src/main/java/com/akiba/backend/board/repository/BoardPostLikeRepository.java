package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.BoardPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardPostLikeRepository extends JpaRepository<BoardPostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);

    Optional<BoardPostLike> findByPostIdAndUserId(Long postId, Long userId);

    void deleteByPostId(Long postId);
}
