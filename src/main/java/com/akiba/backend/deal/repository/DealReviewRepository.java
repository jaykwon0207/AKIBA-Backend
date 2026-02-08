package com.akiba.backend.deal.repository;

import com.akiba.backend.deal.domain.DealReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealReviewRepository extends JpaRepository<DealReview, Long> {

    List<DealReview> findByRevieweeId(Long revieweeId);

    boolean existsByDealIdAndReviewerId(Long dealId, Long reviewerId);
}
