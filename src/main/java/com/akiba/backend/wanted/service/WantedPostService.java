// ========================================================================
// 파일 경로: com/akiba/backend/wanted/service/WantedPostService.java
// 설명: 구해요 관련 모든 비즈니스 로직
//
// [구해요의 특이점]
// - MarketPost(type=WANTED)와 WantedPost 두 테이블에 동시 저장
// - MarketPost에는 공통 정보 (제목, 내용, 상태 등)
// - WantedPost에는 구해요 전용 정보 (희망 가격 범위, 희망 상태)
// - 조회 시 두 테이블을 JOIN해서 가져옴
//
// [주요 기능]
// 1. 구해요 글 작성 (MarketPost + WantedPost 동시 생성)
// 2. 구해요 목록 조회
// 3. 구해요 상세 조회
// 4. 구해요 글 수정
// 5. 구해요 글 삭제
// 6. 인기 구해요 조회
// 7. 구해요 검색
// ========================================================================
package com.akiba.backend.wanted.service;

import com.akiba.backend.wanted.domain.WantedPost;
import com.akiba.backend.wanted.repository.WantedPostRepository;
import com.akiba.backend.media.domain.MediaFile;
import com.akiba.backend.media.repository.MediaFileRepository;
import com.akiba.backend.used.domain.DeliveryMethod;
import com.akiba.backend.used.domain.MarketPost;
import com.akiba.backend.used.domain.MarketPostImage;
import com.akiba.backend.used.domain.MarketPostStatus;
import com.akiba.backend.used.domain.MarketSpecialType;
import com.akiba.backend.used.domain.MarketPostTag;
import com.akiba.backend.used.domain.MarketPostType;
import com.akiba.backend.used.domain.ProductCondition;
import com.akiba.backend.used.domain.Tag;
import com.akiba.backend.used.repository.MarketPostFavoriteRepository;
import com.akiba.backend.used.repository.MarketPostImageRepository;
import com.akiba.backend.used.repository.MarketPostRepository;
import com.akiba.backend.used.repository.MarketPostTagRepository;
import com.akiba.backend.used.repository.TagRepository;
import com.akiba.backend.user.domain.User;
import com.akiba.backend.user.domain.UserProfile;
import com.akiba.backend.user.repository.UserProfileRepository;
import com.akiba.backend.user.repository.UserRepository;
import com.akiba.backend.wanted.dto.request.*;
import com.akiba.backend.wanted.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WantedPostService {

    private final MarketPostRepository marketPostRepository;
    private final WantedPostRepository wantedPostRepository;
    private final MarketPostImageRepository marketPostImageRepository;
    private final MarketPostTagRepository marketPostTagRepository;
    private final MarketPostFavoriteRepository marketPostFavoriteRepository;
    private final TagRepository tagRepository;
    private final MediaFileRepository mediaFileRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    // =========================================================================
    // 1. 구해요 글 작성
    // =========================================================================
    // MarketPost(type=WANTED) 먼저 저장 → WantedPost에 추가 정보 저장
    // 하나라도 실패하면 전체 롤백
    // =========================================================================
    @Transactional
    public Long createPost(Long userId, WantedPostCreateRequest request) {

        // 1) MarketPost 생성 (공통 정보)
        MarketPost marketPost = MarketPost.builder()
                .userId(userId)
                .type(MarketPostType.WANTED)                    // 타입 고정: WANTED
                .productCondition(ProductCondition.USED)        // 구해요는 기본값
                .specialType(parseSpecialType(request.getSpecialType()))
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .deliveryMethod(parseDeliveryMethod(request.getDeliveryMethod()))
                .build();

        MarketPost savedMarketPost = marketPostRepository.save(marketPost);

        // 2) WantedPost 생성 (구해요 전용 정보)
        WantedPost wantedPost = WantedPost.builder()
                .marketPost(savedMarketPost)                    // @MapsId로 MarketPost의 PK를 공유
                .conditionTxt(request.getConditionTxt())
                .build();

        wantedPostRepository.save(wantedPost);

        // 3) 이미지 저장
        saveImages(savedMarketPost.getPostId(), request.getImageMediaIds());

        // 4) 태그 저장
        saveTags(savedMarketPost.getPostId(), request.getTagNames());

        return savedMarketPost.getPostId();
    }

    // =========================================================================
    // 2. 구해요 목록 조회
    // =========================================================================
    // MarketPost(type=WANTED) 기준으로 조회 후
    // WantedPost를 JOIN해서 희망 가격 범위를 가져옴
    // =========================================================================
    public Map<String, Object> getPostList(String sort, int page, int size) {

        Sort sortOption = "popular".equals(sort) ?
                Sort.by("viewCount").descending() :
                Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page, size, sortOption);

        // WANTED 타입 + ACTIVE 상태만 조회
        Page<MarketPost> postPage = marketPostRepository.findByTypeAndStatus(
                MarketPostType.WANTED, MarketPostStatus.ACTIVE, pageable);

        List<WantedPostListResponse> content = postPage.getContent().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalElements", postPage.getTotalElements());
        result.put("totalPages", postPage.getTotalPages());
        result.put("currentPage", postPage.getNumber());
        return result;
    }

    // =========================================================================
    // 3. 구해요 상세 조회
    // =========================================================================
    @Transactional
    public WantedPostDetailResponse getPostDetail(Long postId, Long currentUserId) {

        // 1) MarketPost + WantedPost 조회
        MarketPost marketPost = findActivePostOrThrow(postId);

        WantedPost wantedPost = wantedPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "구해요 정보를 찾을 수 없습니다."));

        // 2) 조회수 +1
        marketPost.increaseViewCount();

        // 3) 이미지 목록
        List<WantedPostDetailResponse.ImageResponse> images = getImageResponses(postId);

        // 4) 찜 수 & 찜 여부
        int favoriteCount = marketPostFavoriteRepository.countByPostId(postId);
        boolean isFavorite = currentUserId != null &&
                marketPostFavoriteRepository.existsByPostIdAndUserId(postId, currentUserId);

        // 5) 작성자 정보
        WantedPostDetailResponse.AuthorResponse author = getAuthorInfo(marketPost.getUserId());

        // 6) 유사 구해요 글 (같은 WANTED 타입의 다른 글)
        List<WantedPostDetailResponse.SimilarWantedResponse> similarPosts = getSimilarWantedPosts(marketPost);

        return WantedPostDetailResponse.builder()
                .postId(postId)
                .title(marketPost.getTitle())
                .content(marketPost.getContent())
                .price(marketPost.getPrice())
                .conditionTxt(wantedPost.getConditionTxt())
                .specialType(marketPost.getSpecialType().name())
                .deliveryMethod(marketPost.getDeliveryMethod())
                .status(marketPost.getStatus().name())
                .viewCount(marketPost.getViewCount())
                .favoriteCount(favoriteCount)
                .isFavorite(isFavorite)
                .createdAt(marketPost.getCreatedAt())
                .images(images)
                .author(author)
                .similarPosts(similarPosts)
                .build();
    }

    // =========================================================================
    // 4. 구해요 글 수정
    // =========================================================================
    @Transactional
    public void updatePost(Long postId, Long userId, WantedPostUpdateRequest request) {

        MarketPost marketPost = findActivePostOrThrow(postId);

        if (!marketPost.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        WantedPost wantedPost = wantedPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "구해요 정보를 찾을 수 없습니다."));

        // MarketPost 수정
        marketPost.update(
                null,
                request.getTitle(),
                request.getContent(),
                request.getPrice(),
                null,
                parseSpecialType(request.getSpecialType()),
                null,
                parseDeliveryMethod(request.getDeliveryMethod()),
                null,
                null
        );

        // WantedPost 수정
        wantedPost.update(request.getConditionTxt());

        // 이미지 교체
        if (request.getImageMediaIds() != null) {
            marketPostImageRepository.deleteByPostId(postId);
            saveImages(postId, request.getImageMediaIds());
        }

        // 태그 교체
        if (request.getTagNames() != null) {
            marketPostTagRepository.deleteByPostId(postId);
            saveTags(postId, request.getTagNames());
        }
    }

    // =========================================================================
    // 5. 구해요 글 삭제
    // =========================================================================
    @Transactional
    public void deletePost(Long postId, Long userId) {

        MarketPost marketPost = findActivePostOrThrow(postId);

        wantedPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "구해요 정보를 찾을 수 없습니다."));

        if (!marketPost.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        marketPost.changeStatus(MarketPostStatus.DELETED);
    }

    // =========================================================================
    // ===== 공통 헬퍼 메서드 =====
    // =========================================================================

    /**
     * MarketPost → WantedPostListResponse 변환
     * WantedPost를 조인해서 희망 가격 범위를 가져옴
     */
    private WantedPostListResponse toListResponse(MarketPost post) {
        // WantedPost에서 희망 가격 범위 가져오기
        WantedPost wantedPost = wantedPostRepository.findById(post.getPostId()).orElse(null);

        // 작성자 닉네임
        String nickname = userRepository.findById(post.getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        // 미리보기 (50자 제한)
        String preview = post.getContent();
        if (preview != null && preview.length() > 50) {
            preview = preview.substring(0, 50) + "...";
        }

        return WantedPostListResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .contentPreview(preview)
                .price(post.getPrice())
                .conditionTxt(wantedPost != null ? wantedPost.getConditionTxt() : null)
                .specialType(post.getSpecialType().name())
                .deliveryMethod(post.getDeliveryMethod())
                .authorNickname(nickname)
                .thumbnailUrl(getThumbnailUrl(post.getPostId()))
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .favoriteCount(marketPostFavoriteRepository.countByPostId(post.getPostId()))
                .build();
    }

    private void saveImages(Long postId, List<Long> mediaIds) {
        if (mediaIds == null || mediaIds.isEmpty()) return;
        for (int i = 0; i < mediaIds.size(); i++) {
            marketPostImageRepository.save(MarketPostImage.builder()
                    .postId(postId)
                    .mediaId(mediaIds.get(i))
                    .sortOrder(i + 1)
                    .build());
        }
    }

    private void saveTags(Long postId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) return;
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
            marketPostTagRepository.save(MarketPostTag.builder()
                    .postId(postId)
                    .tagId(tag.getTagId())
                    .build());
        }
    }

    private String getThumbnailUrl(Long postId) {
        return marketPostImageRepository.findFirstByPostIdOrderBySortOrder(postId)
                .flatMap(img -> mediaFileRepository.findById(img.getMediaId()))
                .map(MediaFile::getUrl)
                .orElse(null);
    }

    private String parseDeliveryMethod(String deliveryMethod) {
        try {
            return DeliveryMethod.fromInput(deliveryMethod).getLabel();
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getReason());
        }
    }

    private MarketSpecialType parseSpecialType(String specialType) {
        try {
            return MarketSpecialType.fromInput(specialType);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 specialType 값입니다.");
        }
    }

    private List<WantedPostDetailResponse.ImageResponse> getImageResponses(Long postId) {
        return marketPostImageRepository.findByPostIdOrderBySortOrder(postId).stream()
                .map(img -> {
                    String url = mediaFileRepository.findById(img.getMediaId())
                            .map(MediaFile::getUrl).orElse(null);
                    return WantedPostDetailResponse.ImageResponse.builder()
                            .mediaId(img.getMediaId())
                            .imageUrl(url)
                            .sortOrder(img.getSortOrder())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private WantedPostDetailResponse.AuthorResponse getAuthorInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);

        return WantedPostDetailResponse.AuthorResponse.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .profileImageUrl(profile != null ? profile.getProfileImageUrl() : null)
                .build();
    }

    /**
     * 유사 구해요 글 조회 (같은 WANTED 타입의 다른 ACTIVE 글)
     */
    private List<WantedPostDetailResponse.SimilarWantedResponse> getSimilarWantedPosts(MarketPost basePost) {
        if (basePost.getCategoryId() == null) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

        List<MarketPost> similarPosts = marketPostRepository.findByTypeAndStatusAndCategoryIdAndPostIdNot(
                MarketPostType.WANTED,
                MarketPostStatus.ACTIVE,
                basePost.getCategoryId(),
                basePost.getPostId(),
                pageable
        );

        return similarPosts.stream()
                .map(p -> {
                    WantedPost wp = wantedPostRepository.findById(p.getPostId()).orElse(null);
                    return WantedPostDetailResponse.SimilarWantedResponse.builder()
                            .postId(p.getPostId())
                            .title(p.getTitle())
                            .conditionTxt(wp != null ? wp.getConditionTxt() : null)
                            .createdAt(p.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private MarketPost findActivePostOrThrow(Long postId) {
        MarketPost marketPost = marketPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        if (marketPost.getStatus() == MarketPostStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return marketPost;
    }
}
