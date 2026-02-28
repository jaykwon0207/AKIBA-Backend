// ========================================================================
// 파일 경로: com/akiba/backend/market/dto/request/MarketPostStatusRequest.java
// 설명: 게시글 상태 변경 (판매중/예약중/판매완료 등)
// ========================================================================
package com.akiba.backend.market.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MarketPostStatusRequest {

    private String status;  // "ACTIVE", "RESERVED", "SOLD", "CLOSED"
}
