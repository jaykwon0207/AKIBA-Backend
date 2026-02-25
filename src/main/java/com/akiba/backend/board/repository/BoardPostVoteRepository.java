package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.AuthenticityVoteChoice;
import com.akiba.backend.board.domain.BoardPostVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardPostVoteRepository extends JpaRepository<BoardPostVote, Long> {

    Optional<BoardPostVote> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostIdAndChoice(Long postId, AuthenticityVoteChoice choice);

    void deleteByPostId(Long postId);
}
