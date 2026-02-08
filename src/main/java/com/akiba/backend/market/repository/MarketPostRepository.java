package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.MarketPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketPostRepository extends JpaRepository<MarketPost, Long> {}

