package com.akiba.backend.market.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MarketPostSimilarResponse {
    private Long postId;
    private String type;
    private String title;
    private Integer price;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private int similarityScore;
    private List<String> reasonKeywords;
}

