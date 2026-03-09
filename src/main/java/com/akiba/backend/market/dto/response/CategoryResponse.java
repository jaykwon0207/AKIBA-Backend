// ========================================================================
// 파일 경로: com/akiba/backend/market/dto/response/CategoryResponse.java
// 설명: 카테고리 트리 구조 응답
// ========================================================================
package com.akiba.backend.market.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CategoryResponse {

    private Long categoryId;
    private String name;
    private int sortOrder;
    private List<CategoryResponse> children;
}
