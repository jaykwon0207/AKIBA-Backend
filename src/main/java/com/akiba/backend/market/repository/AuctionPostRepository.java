package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.AuctionPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionPostRepository extends JpaRepository<AuctionPost, Long> {}

