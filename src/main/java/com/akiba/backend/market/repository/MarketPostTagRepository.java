package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.MarketPostTag;
import com.akiba.backend.market.domain.MarketPostTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketPostTagRepository extends JpaRepository<MarketPostTag, MarketPostTagId> {}

