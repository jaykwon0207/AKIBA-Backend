package com.akiba.backend.user.repository;

import com.akiba.backend.user.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// UserProfileRepository.java
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // 이 줄을 추가해야 서비스의 360행 에러가 사라집니다.
    Optional<UserProfile> findByUserId(Long userId);
}
