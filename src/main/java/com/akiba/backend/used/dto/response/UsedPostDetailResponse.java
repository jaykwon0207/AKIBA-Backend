package com.akiba.backend.used.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UsedPostDetailResponse {

    private Long postId;
    private String type;
    private String title;
    private String content;
    private Integer price;
    private String productCondition;
    private String specialType;
    private String status;
    private String deliveryMethod;
    private String purchaseSource;
    private Long receiptMediaId;
    private int viewCount;
    private int favoriteCount;
    private boolean isFavorite;
    private LocalDateTime createdAt;
    private List<ImageResponse> images;
    private List<String> tags;
    private SellerResponse seller;

    @Getter
    @Builder
    public static class ImageResponse {
        private Long mediaId;
        private String imageUrl;
        private int sortOrder;
    }

    @Getter
    @Builder
    public static class SellerResponse {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private String bio;
        private int dealCount;
        private int reviewCount;
    }
}
