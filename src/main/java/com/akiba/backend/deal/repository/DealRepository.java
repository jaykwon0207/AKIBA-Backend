package com.akiba.backend.deal.repository;

import com.akiba.backend.deal.domain.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findBySellerId(Long sellerId);

    List<Deal> findByBuyerId(Long buyerId);

    boolean existsByPostId(Long postId);

    int countBySellerIdOrBuyerId(Long sellerId, Long buyerId);
}
