package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.MarketPostImage;
import com.akiba.backend.market.domain.MarketPostImageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketPostImageRepository extends JpaRepository<MarketPostImage, MarketPostImageId> {

    List<MarketPostImage> findByPostId(Long postId);
}

