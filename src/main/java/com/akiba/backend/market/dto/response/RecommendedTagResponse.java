package com.akiba.backend.market.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendedTagResponse {
    private int rank;
    private String tagName;
    private long count;
}

