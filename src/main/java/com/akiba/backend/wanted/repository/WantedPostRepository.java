package com.akiba.backend.wanted.repository;

import com.akiba.backend.wanted.domain.WantedPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WantedPostRepository extends JpaRepository<WantedPost, Long> {}
