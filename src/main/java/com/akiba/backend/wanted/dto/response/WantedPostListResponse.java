// ========================================================================
// 파일 경로: com/akiba/backend/wanted/dto/response/WantedPostListResponse.java
// 설명: 구해요 목록 페이지 카드에 표시할 요약 정보
// ========================================================================
package com.akiba.backend.wanted.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class WantedPostListResponse {

    private Long postId;
    private String title;
    private String contentPreview;       // 내용 미리보기 (앞 50자)
    private Integer price;
    private String conditionTxt;
    private String specialType;
    private String deliveryMethod;
    private String authorNickname;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private int viewCount;
    private int favoriteCount;
}
