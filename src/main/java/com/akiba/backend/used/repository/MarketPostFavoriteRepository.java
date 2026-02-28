package com.akiba.backend.used.repository;

import com.akiba.backend.used.domain.MarketPostFavorite;
import com.akiba.backend.used.domain.MarketPostFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MarketPostFavoriteRepository extends JpaRepository<MarketPostFavorite, MarketPostFavoriteId> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    List<MarketPostFavorite> findByUserId(Long userId);

    // 게시글의 찜 수 카운트
    int countByPostId(Long postId);


}
