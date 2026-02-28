// ========================================================================
// 파일 경로: com/akiba/backend/auction/dto/response/AuctionPostListResponse.java
// 설명: 경매 목록 페이지 카드에 표시할 요약 정보
//       남은 시간, 현재 최고가, 입찰 수 포함
// ========================================================================
package com.akiba.backend.auction.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionPostListResponse {

    private Long postId;
    private String title;
    private String specialType;
    private int startPrice;            // 시작가
    private Integer currentPrice;      // 현재 최고 입찰가
    private Integer buyNowPrice;       // 즉시구매가
    private int bidCount;              // 입찰 수
    private String thumbnailUrl;
    private LocalDateTime endsAt;      // 경매 종료 시간
    private String status;             // ACTIVE, ENDED, SOLD
    private int viewCount;
    private int favoriteCount;
}
