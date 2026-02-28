package com.akiba.backend.market.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentViewedPostResponse {
    private Long postId;
    private String title;
    private Integer price;
    private String thumbnailUrl;
}
