// ========================================================================
// 파일 경로: com/akiba/backend/auction/dto/request/BidRequest.java
// 설명: 입찰 시 프론트에서 보내는 데이터
// ========================================================================
package com.akiba.backend.auction.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidRequest {

    @Positive(message = "bidAmount는 1 이상이어야 합니다.")
    private int bidAmount;  // 입찰 금액
}
