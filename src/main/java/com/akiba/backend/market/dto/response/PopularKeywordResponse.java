// ========================================================================
// 파일 경로: com/akiba/backend/market/dto/response/PopularKeywordResponse.java
// 설명: 인기 검색어 TOP 10 응답
// ========================================================================
package com.akiba.backend.market.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PopularKeywordResponse {

    private String updatedAt;         // "오후 8시 업데이트"
    private List<KeywordItem> keywords;

    @Getter
    @Builder
    public static class KeywordItem {
        private int rank;
        private String keyword;
        private String trend;         // "UP", "DOWN", "SAME"
    }
}
