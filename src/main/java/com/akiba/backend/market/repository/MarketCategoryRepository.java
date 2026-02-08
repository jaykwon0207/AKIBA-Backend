package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.MarketCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketCategoryRepository extends JpaRepository<MarketCategory, Long> {}
