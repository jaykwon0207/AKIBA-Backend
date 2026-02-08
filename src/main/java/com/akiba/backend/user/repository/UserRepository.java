package com.akiba.backend.user.repository;

import com.akiba.backend.user.domain.User;
import com.akiba.backend.user.domain.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndOauthId(AuthProvider provider, String oauthId);

    Optional<User> findByEmail(String email);
}
