// ========================================================================
// 파일 경로: com/akiba/backend/auction/dto/response/BidResponse.java
// 설명: 입찰 성공 시 반환하는 정보
// ========================================================================
package com.akiba.backend.auction.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class BidResponse {

    private Long bidId;
    private Long postId;
    private int bidAmount;
    private int bidCount;              // 전체 입찰 수
    private LocalDateTime bidAt;
    private String message;
}
