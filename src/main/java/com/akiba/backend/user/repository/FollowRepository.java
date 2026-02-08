package com.akiba.backend.user.repository;

import com.akiba.backend.user.domain.Follow;
import com.akiba.backend.user.domain.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    long countByFollowerId(Long followerId);

    long countByFollowingId(Long followingId);
}
