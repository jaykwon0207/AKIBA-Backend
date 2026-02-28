package com.akiba.backend.used.service;

import com.akiba.backend.market.dto.request.MarketPostCreateRequest;
import com.akiba.backend.market.dto.request.MarketPostStatusRequest;
import com.akiba.backend.market.dto.request.MarketPostUpdateRequest;
import com.akiba.backend.market.dto.response.MarketPostDetailResponse;
import com.akiba.backend.market.dto.response.MarketPostSimpleResponse;
import com.akiba.backend.market.service.MarketPostService;
import com.akiba.backend.used.dto.request.UsedPostCreateRequest;
import com.akiba.backend.used.dto.request.UsedPostStatusRequest;
import com.akiba.backend.used.dto.request.UsedPostUpdateRequest;
import com.akiba.backend.used.dto.response.UsedPostDetailResponse;
import com.akiba.backend.used.dto.response.UsedPostSimpleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsedPostService {

    private final MarketPostService marketPostService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long createPost(Long userId, UsedPostCreateRequest request) {
        return marketPostService.createPost(userId, toMarketCreateRequest(request));
    }

    public Map<String, Object> getPostList(Long categoryId, String status, String sort, int page, int size) {
        return marketPostService.getPostList("USED", categoryId, status, sort, page, size);
    }

    @Transactional
    public UsedPostDetailResponse getPostDetail(Long postId, Long currentUserId) {
        return toUsedDetailResponse(marketPostService.getPostDetail(postId, currentUserId));
    }

    @Transactional
    public void updatePost(Long postId, Long userId, UsedPostUpdateRequest request) {
        marketPostService.updatePost(postId, userId, toMarketUpdateRequest(request));
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        marketPostService.deletePost(postId, userId);
    }

    @Transactional
    public void changeStatus(Long postId, Long userId, UsedPostStatusRequest request) {
        marketPostService.changeStatus(postId, userId, toMarketStatusRequest(request));
    }

    public List<UsedPostSimpleResponse> getPopularPosts(int limit) {
        return marketPostService.getPopularPosts("USED", limit).stream()
                .map(this::toUsedSimpleResponse)
                .toList();
    }

    private MarketPostCreateRequest toMarketCreateRequest(UsedPostCreateRequest request) {
        MarketPostCreateRequest mapped = objectMapper.convertValue(request, MarketPostCreateRequest.class);
        setField(mapped, "type", "USED");
        return mapped;
    }

    private MarketPostUpdateRequest toMarketUpdateRequest(UsedPostUpdateRequest request) {
        MarketPostUpdateRequest mapped = objectMapper.convertValue(request, MarketPostUpdateRequest.class);
        setField(mapped, "type", "USED");
        return mapped;
    }

    private MarketPostStatusRequest toMarketStatusRequest(UsedPostStatusRequest request) {
        return objectMapper.convertValue(request, MarketPostStatusRequest.class);
    }

    private UsedPostSimpleResponse toUsedSimpleResponse(MarketPostSimpleResponse source) {
        return UsedPostSimpleResponse.builder()
                .postId(source.getPostId())
                .title(source.getTitle())
                .price(source.getPrice())
                .thumbnailUrl(source.getThumbnailUrl())
                .type(source.getType())
                .build();
    }

    private UsedPostDetailResponse toUsedDetailResponse(MarketPostDetailResponse source) {
        List<UsedPostDetailResponse.ImageResponse> images = source.getImages() == null ? List.of() :
                source.getImages().stream()
                        .map(image -> UsedPostDetailResponse.ImageResponse.builder()
                                .mediaId(image.getMediaId())
                                .imageUrl(image.getImageUrl())
                                .sortOrder(image.getSortOrder())
                                .build())
                        .toList();

        UsedPostDetailResponse.SellerResponse seller = null;
        if (source.getSeller() != null) {
            seller = UsedPostDetailResponse.SellerResponse.builder()
                    .userId(source.getSeller().getUserId())
                    .nickname(source.getSeller().getNickname())
                    .profileImageUrl(source.getSeller().getProfileImageUrl())
                    .bio(source.getSeller().getBio())
                    .dealCount(source.getSeller().getDealCount())
                    .reviewCount(source.getSeller().getReviewCount())
                    .build();
        }

        return UsedPostDetailResponse.builder()
                .postId(source.getPostId())
                .type(source.getType())
                .title(source.getTitle())
                .content(source.getContent())
                .price(source.getPrice())
                .productCondition(source.getProductCondition())
                .specialType(source.getSpecialType())
                .status(source.getStatus())
                .deliveryMethod(source.getDeliveryMethod())
                .purchaseSource(source.getPurchaseSource())
                .receiptMediaId(source.getReceiptMediaId())
                .viewCount(source.getViewCount())
                .favoriteCount(source.getFavoriteCount())
                .isFavorite(source.isFavorite())
                .createdAt(source.getCreatedAt())
                .images(images)
                .tags(source.getTags())
                .seller(seller)
                .build();
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("필드 매핑에 실패했습니다: " + fieldName, e);
        }
    }
}
