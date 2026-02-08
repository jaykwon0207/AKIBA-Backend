package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.WantedPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WantedPostRepository extends JpaRepository<WantedPost, Long> {}
