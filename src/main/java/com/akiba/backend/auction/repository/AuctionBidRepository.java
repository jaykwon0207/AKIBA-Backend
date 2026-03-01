package com.akiba.backend.auction.repository;

import com.akiba.backend.auction.domain.AuctionBid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {
    // 최고 입찰 조회 (입찰 금액 내림차순 첫 번째)
    Optional<AuctionBid> findTopByPostIdOrderByBidAmountDesc(Long postId);

    // 특정 경매의 입찰 내역 (페이지네이션)
    Page<AuctionBid> findByPostId(Long postId, Pageable pageable);

    // 특정 유저의 입찰 내역 (페이지네이션)
    Page<AuctionBid> findByUserId(Long userId, Pageable pageable);

    // 특정 유저가 해당 경매에 입찰했는지 여부
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}

