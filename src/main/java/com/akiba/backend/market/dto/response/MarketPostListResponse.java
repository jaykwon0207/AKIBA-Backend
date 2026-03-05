// ========================================================================
// 파일 경로: com/akiba/backend/market/dto/response/MarketPostListResponse.java
// 설명: 중고거래 목록 조회 시 각 게시글 요약 정보 (카드 형태)
// ========================================================================
package com.akiba.backend.market.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class MarketPostListResponse {

    private Long postId;
    private String type;              // USED, LIMITED
    private String title;
    private Integer price;
    private String productCondition;
    private String specialType;
    private String status;
    private String thumbnailUrl;      // 첫 번째 이미지 URL
    private LocalDateTime createdAt;
    private int viewCount;
    private int favoriteCount;
}
