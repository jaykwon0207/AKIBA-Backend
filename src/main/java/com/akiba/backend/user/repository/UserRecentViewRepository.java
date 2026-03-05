package com.akiba.backend.user.repository;

import com.akiba.backend.user.domain.UserRecentView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRecentViewRepository extends JpaRepository<UserRecentView, Long> {

    Optional<UserRecentView> findByUserIdAndPostId(Long userId, Long postId);

    Page<UserRecentView> findByUserIdOrderByViewedAtDesc(Long userId, Pageable pageable);
}
