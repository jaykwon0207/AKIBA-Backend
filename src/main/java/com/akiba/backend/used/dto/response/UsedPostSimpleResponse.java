package com.akiba.backend.used.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsedPostSimpleResponse {
    private Long postId;
    private String title;
    private Integer price;
    private String thumbnailUrl;
    private String type;
}
