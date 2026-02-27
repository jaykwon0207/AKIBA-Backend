package com.akiba.backend.user.repository;

import com.akiba.backend.user.domain.User;
import com.akiba.backend.user.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);

}
