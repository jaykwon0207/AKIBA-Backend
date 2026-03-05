package com.akiba.backend.auction.repository;

import com.akiba.backend.auction.domain.AuctionPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;

public interface AuctionPostRepository extends JpaRepository<AuctionPost, Long> {
    List<AuctionPost> findByEndsAtAfterOrderByEndsAtAsc(LocalDateTime now, Pageable pageable);

    // 종료 시간 지났지만 아직 낙찰자 없는 경매 (스케줄러용)
    List<AuctionPost> findByEndsAtBeforeAndWinnerUserIdIsNull(LocalDateTime now);

    // 낙찰자 기준 조회
    List<AuctionPost> findByWinnerUserIdOrderByEndsAtDesc(Long winnerUserId);

}
