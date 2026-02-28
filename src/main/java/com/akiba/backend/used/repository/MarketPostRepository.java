package com.akiba.backend.used.repository;

import com.akiba.backend.used.domain.MarketPost;
import com.akiba.backend.used.domain.MarketPostStatus;
import com.akiba.backend.used.domain.MarketPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MarketPostRepository extends JpaRepository<MarketPost, Long>, JpaSpecificationExecutor<MarketPost> {

    // 타입 + 상태로 필터링 (목록 조회용)
    Page<MarketPost> findByTypeAndStatus(MarketPostType type, MarketPostStatus status, Pageable pageable);

    // 상태로만 필터링
    Page<MarketPost> findByStatus(MarketPostStatus status, Pageable pageable);

    Page<MarketPost> findByStatusAndPostIdNot(MarketPostStatus status, Long postId, Pageable pageable);

    // 검색 (타입 + 상태 + 키워드)
    Page<MarketPost> findByTypeAndStatusAndTitleContainingOrTypeAndStatusAndContentContaining(
            MarketPostType type, MarketPostStatus status,
            String titleKeyword, MarketPostType contentType, MarketPostStatus contentStatus,
            String contentKeyword, Pageable pageable);

    // 검색 (상태 + 키워드)
    Page<MarketPost> findByStatusAndTitleContainingOrStatusAndContentContaining(
            MarketPostStatus status, String titleKeyword, MarketPostStatus contentStatus,
            String contentKeyword, Pageable pageable);

    // 유사 상품 (같은 타입 + 상태 + 카테고리, 본인 제외)
    List<MarketPost> findByTypeAndStatusAndCategoryIdAndPostIdNot(
            MarketPostType type, MarketPostStatus status,
            Long categoryId, Long postId, Pageable pageable);

    // 내 게시글 조회 (타입 + 작성자 + 특정 상태 제외)
    List<MarketPost> findByTypeAndUserIdAndStatusNot(
            MarketPostType type, Long userId, MarketPostStatus status);

}
