package com.akiba.backend.used.repository;

import com.akiba.backend.used.domain.MarketPostImage;
import com.akiba.backend.used.domain.MarketPostImageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarketPostImageRepository extends JpaRepository<MarketPostImage, MarketPostImageId> {

    List<MarketPostImage> findByPostId(Long postId);

    // 게시글의 이미지를 순서대로 조회
    List<MarketPostImage> findByPostIdOrderBySortOrder(Long postId);

    // 첫 번째 이미지(썸네일) 조회
    Optional<MarketPostImage> findFirstByPostIdOrderBySortOrder(Long postId);

    // 게시글의 이미지 전체 삭제 (수정 시 교체용)
    void deleteByPostId(Long postId);
}

