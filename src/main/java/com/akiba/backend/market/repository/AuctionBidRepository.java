package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.AuctionBid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {}

