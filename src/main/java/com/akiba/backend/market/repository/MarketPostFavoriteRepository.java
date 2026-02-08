package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.MarketPostFavorite;
import com.akiba.backend.market.domain.MarketPostFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketPostFavoriteRepository extends JpaRepository<MarketPostFavorite, MarketPostFavoriteId> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    List<MarketPostFavorite> findByUserId(Long userId);
}
