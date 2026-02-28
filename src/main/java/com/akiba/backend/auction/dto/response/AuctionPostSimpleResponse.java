// ========================================================================
// 파일 경로: com/akiba/backend/auction/dto/response/AuctionPostSimpleResponse.java
// 설명: 인기 경매, 유사 경매 등 간략 카드용 응답
//       AuctionPostListResponse보다 더 가벼운 버전
// ========================================================================
package com.akiba.backend.auction.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionPostSimpleResponse {
    private Long postId;
    private String title;
    private String specialType;
    private int startPrice;            // ← 이 필드 추가
    private Integer currentPrice;      // currentHighestBid → currentPrice
    private int bidCount;
    private Integer viewCount;         // 인기 경매에서만 사용
    private String thumbnailUrl;
    private LocalDateTime endsAt;
}
