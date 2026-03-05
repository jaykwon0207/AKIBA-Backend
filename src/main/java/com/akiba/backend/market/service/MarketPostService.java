// ========================================================================
// 파일 경로: com/akiba/backend/market/service/MarketPostService.java
// 설명: 중고거래/특전한정판 관련 모든 비즈니스 로직을 처리하는 핵심 서비스
//
// [주요 기능]
// 1. 게시글 CRUD (작성, 조회, 수정, 삭제, 상태변경)
// 2. 목록 조회 (페이지네이션, 정렬)
// 3. 상세 조회 (이미지, 태그, 판매자정보 포함)
// 4. 검색 (키워드 검색)
// 5. 인기 매물 / 유사 상품 조회
// 6. 카테고리 조회
// ========================================================================
package com.akiba.backend.market.service;

import com.akiba.backend.market.dto.request.*;
import com.akiba.backend.market.dto.response.*;
import com.akiba.backend.used.domain.*;
import com.akiba.backend.used.repository.*;
import com.akiba.backend.user.domain.User;
import com.akiba.backend.user.domain.UserProfile;
import com.akiba.backend.user.repository.UserProfileRepository;
import com.akiba.backend.user.repository.UserRepository;
import com.akiba.backend.user.service.UserRecentViewService;
import com.akiba.backend.search.service.SearchKeywordService;
import com.akiba.backend.deal.repository.DealRepository;
import com.akiba.backend.deal.repository.DealReviewRepository;
import com.akiba.backend.media.domain.MediaFile;
import com.akiba.backend.media.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketPostService {

    private final MarketPostRepository marketPostRepository;
    private final MarketPostImageRepository marketPostImageRepository;
    private final MarketPostTagRepository marketPostTagRepository;
    private final MarketPostFavoriteRepository marketPostFavoriteRepository;
    private final TagRepository tagRepository;
    private final MarketCategoryRepository marketCategoryRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final DealRepository dealRepository;
    private final DealReviewRepository dealReviewRepository;
    private final MediaFileRepository mediaFileRepository;
    private final UserRecentViewService userRecentViewService;
    private final SearchKeywordService searchKeywordService;

    // =========================================================================
    // 1. 게시글 작성
    // =========================================================================
    @Transactional
    public Long createPost(Long userId, MarketPostCreateRequest request) {
        validateCategoryId(request.getCategoryId());

        MarketPost post = MarketPost.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .type(parseMarketPostType(request.getType()))
                .productCondition(parseProductCondition(request.getProductCondition()))
                .specialType(parseSpecialType(request.getSpecialType()))
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .deliveryMethod(parseDeliveryMethod(request.getDeliveryMethod()))
                .purchaseSource(request.getPurchaseSource())
                .receiptMediaId(request.getReceiptMediaId())
                .build();

        MarketPost savedPost = marketPostRepository.save(post);

        saveImages(savedPost.getPostId(), request.getImageMediaIds());
        saveTags(savedPost.getPostId(), request.getTagNames());

        return savedPost.getPostId();
    }

    // =========================================================================
    // 2. 게시글 목록 조회
    // =========================================================================
    public Map<String, Object> getPostList(String type, Long categoryId, String status,
                                            String sort, int page, int size) {

        Sort sortOption = buildSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortOption);

        Page<MarketPost> postPage;
        if (type != null && !type.isEmpty()) {
            MarketPostType postType = MarketPostType.valueOf(type);
            MarketPostStatus postStatus = (status != null) ?
                    MarketPostStatus.valueOf(status) : MarketPostStatus.ACTIVE;
            postPage = marketPostRepository.findByTypeAndStatus(postType, postStatus, pageable);
        } else {
            postPage = marketPostRepository.findByStatus(MarketPostStatus.ACTIVE, pageable);
        }

        List<MarketPostListResponse> content = postPage.getContent().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());

        return buildPageResult(content, postPage);
    }

    // =========================================================================
    // 2-1. 통합 글 목록 조회 (/api/market/posts)
    // =========================================================================
    public Map<String, Object> getIntegratedPostList(String type, String status, String keyword,
                                                     boolean onlyActive, boolean unOpenedOnly,
                                                     String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));

        MarketPostType postType = null;
        if (type != null && !type.isBlank()) {
            postType = parseMarketPostType(type.trim().toUpperCase());
            if (postType != MarketPostType.USED
                    && postType != MarketPostType.WANTED
                    && postType != MarketPostType.AUCTION) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "type은 USED, WANTED, AUCTION만 가능합니다."
                );
            }
        }

        MarketPostStatus requestedStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                requestedStatus = MarketPostStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 status 값입니다.");
            }
        }

        Set<String> keywordPatterns = new LinkedHashSet<>();
        if (keyword != null && !keyword.isBlank()) {
            String normalizedKeyword = keyword.trim().replaceAll("\\s+", " ");
            keywordPatterns.add("%" + normalizedKeyword.toLowerCase() + "%");
            Arrays.stream(normalizedKeyword.split(" "))
                    .map(String::trim)
                    .filter(token -> !token.isEmpty())
                    .map(token -> "%" + token.toLowerCase() + "%")
                    .forEach(keywordPatterns::add);
        }

        MarketPostType finalPostType = postType;
        MarketPostStatus finalRequestedStatus = requestedStatus;
        Specification<MarketPost> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (!keywordPatterns.isEmpty()) {
                List<jakarta.persistence.criteria.Predicate> titlePredicates = keywordPatterns.stream()
                        .map(pattern -> cb.like(cb.lower(root.get("title")), pattern))
                        .toList();
                predicates.add(cb.or(titlePredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
            }

            if (finalPostType != null) {
                predicates.add(cb.equal(root.get("type"), finalPostType));
            }

            if (onlyActive) {
                predicates.add(cb.equal(root.get("status"), MarketPostStatus.ACTIVE));
            } else if (finalRequestedStatus != null) {
                predicates.add(cb.equal(root.get("status"), finalRequestedStatus));
            } else {
                predicates.add(cb.notEqual(root.get("status"), MarketPostStatus.DELETED));
            }

            if (unOpenedOnly) {
                predicates.add(cb.equal(root.get("productCondition"), ProductCondition.NEW));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<MarketPost> postPage = marketPostRepository.findAll(spec, pageable);
        List<MarketPostListResponse> content = postPage.getContent().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());

        return buildPageResult(content, postPage);
    }

    // =========================================================================
    // 3. 게시글 상세 조회
    // =========================================================================
    @Transactional
    public MarketPostDetailResponse getPostDetail(Long postId, Long currentUserId) {

        MarketPost post = findPostOrThrow(postId);
        post.increaseViewCount();
        userRecentViewService.touchRecentView(currentUserId, postId);

        List<MarketPostDetailResponse.ImageResponse> images = getImageResponses(postId);
        List<String> tags = getTagNames(postId);

        int favoriteCount = marketPostFavoriteRepository.countByPostId(postId);
        boolean isFavorite = currentUserId != null &&
                marketPostFavoriteRepository.existsByPostIdAndUserId(postId, currentUserId);

        MarketPostDetailResponse.SellerResponse seller = getSellerInfo(post);

        return MarketPostDetailResponse.builder()
                .postId(post.getPostId())
                .type(post.getType().name())
                .title(post.getTitle())
                .content(post.getContent())
                .price(post.getPrice())
                .productCondition(toClientCondition(post.getProductCondition()))
                .specialType(post.getSpecialType().name())
                .status(post.getStatus().name())
                .deliveryMethod(post.getDeliveryMethod())
                .purchaseSource(post.getPurchaseSource())
                .receiptMediaId(post.getReceiptMediaId())
                .viewCount(post.getViewCount())
                .favoriteCount(favoriteCount)
                .isFavorite(isFavorite)
                .createdAt(post.getCreatedAt())
                .images(images)
                .tags(tags)
                .seller(seller)
                .build();
    }

    // =========================================================================
    // 4. 게시글 수정
    // =========================================================================
    @Transactional
    public void updatePost(Long postId, Long userId, MarketPostUpdateRequest request) {

        MarketPost post = findPostOrThrow(postId);
        validateOwner(post, userId, "수정");
        validateCategoryId(request.getCategoryId());

        post.update(
                parseMarketPostType(request.getType()),
                request.getTitle(),
                request.getContent(),
                request.getPrice(),
                parseProductCondition(request.getProductCondition()),
                parseSpecialType(request.getSpecialType()),
                request.getCategoryId(),
                parseDeliveryMethod(request.getDeliveryMethod()),
                request.getPurchaseSource(),
                request.getReceiptMediaId()
        );

        if (request.getImageMediaIds() != null) {
            marketPostImageRepository.deleteByPostId(postId);
            saveImages(postId, request.getImageMediaIds());
        }

        if (request.getTagNames() != null) {
            marketPostTagRepository.deleteByPostId(postId);
            saveTags(postId, request.getTagNames());
        }
    }

    // =========================================================================
    // 5. 게시글 삭제 (소프트 삭제)
    // =========================================================================
    @Transactional
    public void deletePost(Long postId, Long userId) {
        MarketPost post = findPostOrThrow(postId);
        validateOwner(post, userId, "삭제");
        post.changeStatus(MarketPostStatus.DELETED);
    }

    // =========================================================================
    // 6. 게시글 상태 변경
    // =========================================================================
    @Transactional
    public void changeStatus(Long postId, Long userId, MarketPostStatusRequest request) {
        MarketPost post = findPostOrThrow(postId);
        validateOwner(post, userId, "상태 변경");
        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status는 필수입니다.");
        }

        MarketPostStatus targetStatus;
        try {
            targetStatus = MarketPostStatus.valueOf(request.getStatus().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "유효하지 않은 status 값입니다. (ACTIVE, RESERVED, SOLD, CLOSED)"
            );
        }

        if (targetStatus == MarketPostStatus.DELETED || targetStatus == MarketPostStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "상태 변경 API에서는 ACTIVE, RESERVED, SOLD, CLOSED만 허용됩니다."
            );
        }

        post.changeStatus(targetStatus);
    }

    // =========================================================================
    // 7. 인기 매물 조회
    // =========================================================================
    public List<MarketPostSimpleResponse> getPopularPosts(String type, int limit) {
        if (type == null || type.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type은 필수입니다.");
        }

        MarketPostType postType = parseMarketPostType(type);
        if (postType != MarketPostType.USED && postType != MarketPostType.WANTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type은 USED 또는 WANTED만 가능합니다.");
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by("viewCount").descending());
        List<MarketPost> posts = marketPostRepository.findByTypeAndStatus(
                postType, MarketPostStatus.ACTIVE, pageable).getContent();

        return posts.stream().map(this::toSimpleResponse).collect(Collectors.toList());
    }

    // =========================================================================
    // 8. 추천 검색 태그 조회
    // =========================================================================
    public List<RecommendedTagResponse> getRecommendedTags(String type, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);

        List<MarketPostTagRepository.RecommendedTagProjection> rows;
        if (type == null || type.isBlank()) {
            rows = marketPostTagRepository.findRecommendedTagsForActivePosts(PageRequest.of(0, safeLimit));
        } else {
            MarketPostType postType = parseMarketPostType(type.trim().toUpperCase());
            if (postType != MarketPostType.USED
                    && postType != MarketPostType.WANTED
                    && postType != MarketPostType.AUCTION) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "type은 USED, WANTED, AUCTION만 가능합니다."
                );
            }
            rows = marketPostTagRepository.findRecommendedTagsForActivePostsByType(
                    postType.name(),
                    PageRequest.of(0, safeLimit)
            );
        }

        List<RecommendedTagResponse> tags = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            MarketPostTagRepository.RecommendedTagProjection row = rows.get(i);
            tags.add(RecommendedTagResponse.builder()
                    .rank(i + 1)
                    .tagName(row.getTagName())
                    .count(row.getUseCount() != null ? row.getUseCount() : 0L)
                    .build());
        }
        return tags;
    }

    // =========================================================================
    // 9. 최근 본 상품 조회
    // =========================================================================
    public List<RecentViewedPostResponse> getRecentViews(Long userId, int limit) {
        return userRecentViewService.findRecentPostIds(userId, limit).stream()
                .map(postId -> marketPostRepository.findById(postId).orElse(null))
                .filter(Objects::nonNull)
                .filter(post -> post.getStatus() == MarketPostStatus.ACTIVE)
                .map(post -> RecentViewedPostResponse.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .price(post.getPrice())
                        .thumbnailUrl(getThumbnailUrl(post.getPostId()))
                        .build())
                .collect(Collectors.toList());
    }

    // =========================================================================
    // 10. 마켓 검색
    // =========================================================================
    public Map<String, Object> searchPosts(String keyword, String type, boolean onlyActive,
                                           boolean unOpenedOnly, String sort, int page, int size) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "keyword는 필수입니다.");
        }

        searchKeywordService.recordKeyword(normalizedKeyword);
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));

        MarketPostType postType = null;
        if (type != null && !type.isBlank()) {
            postType = parseMarketPostType(type.trim().toUpperCase());
            if (postType != MarketPostType.USED
                    && postType != MarketPostType.WANTED
                    && postType != MarketPostType.AUCTION) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "type은 USED, WANTED, AUCTION만 가능합니다."
                );
            }
        }

        Set<String> keywordPatterns = new LinkedHashSet<>();
        keywordPatterns.add("%" + normalizedKeyword.toLowerCase() + "%");
        Arrays.stream(normalizedKeyword.split(" "))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(token -> "%" + token.toLowerCase() + "%")
                .forEach(keywordPatterns::add);

        MarketPostType finalPostType = postType;
        Specification<MarketPost> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            List<jakarta.persistence.criteria.Predicate> titlePredicates = keywordPatterns.stream()
                    .map(pattern -> cb.like(cb.lower(root.get("title")), pattern))
                    .toList();
            predicates.add(cb.or(titlePredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));

            if (finalPostType != null) {
                predicates.add(cb.equal(root.get("type"), finalPostType));
            }

            if (onlyActive) {
                predicates.add(cb.equal(root.get("status"), MarketPostStatus.ACTIVE));
            } else {
                predicates.add(cb.notEqual(root.get("status"), MarketPostStatus.DELETED));
            }

            if (unOpenedOnly) {
                predicates.add(cb.equal(root.get("productCondition"), ProductCondition.NEW));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<MarketPost> postPage = marketPostRepository.findAll(spec, pageable);

        List<MarketPostListResponse> content = postPage.getContent().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());

        return buildPageResult(content, postPage);
    }


    // =========================================================================
    // 12. 카테고리 목록 조회
    // =========================================================================
    public List<CategoryResponse> getCategories() {
        List<MarketCategory> all = marketCategoryRepository.findAll();

        return all.stream()
                .filter(c -> c.getParentId() == null)
                .map(parent -> CategoryResponse.builder()
                        .categoryId(parent.getCategoryId())
                        .name(parent.getName())
                        .sortOrder(parent.getSortOrder())
                        .children(all.stream()
                                .filter(c -> parent.getCategoryId().equals(c.getParentId()))
                                .map(child -> CategoryResponse.builder()
                                        .categoryId(child.getCategoryId())
                                        .name(child.getName())
                                        .sortOrder(child.getSortOrder())
                                        .children(Collections.emptyList())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<MarketPostSimilarResponse> getSimilarPostsByKeywordScore(Long postId, int limit) {
        MarketPost sourcePost = findPostOrThrow(postId);
        int safeLimit = Math.min(Math.max(limit, 1), 20);

        Pageable candidatePageable = PageRequest.of(0, 200, Sort.by("createdAt").descending());
        List<MarketPost> candidates = marketPostRepository
                .findByStatusAndPostIdNot(MarketPostStatus.ACTIVE, postId, candidatePageable)
                .getContent();

        if (candidates.isEmpty()) {
            return List.of();
        }

        Set<String> sourceTitleTokens = tokenizeKeywords(sourcePost.getTitle());
        Set<String> sourceTagTokens = getTagNames(postId).stream()
                .map(this::normalizeKeyword)
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet());

        Set<String> sourceAllTokens = new HashSet<>(sourceTitleTokens);
        sourceAllTokens.addAll(sourceTagTokens);

        Map<Long, List<String>> candidateTagMap = getTagNamesByPostIds(
                candidates.stream().map(MarketPost::getPostId).toList()
        );

        List<MarketPostSimilarResponse> scored = new ArrayList<>();
        for (MarketPost candidate : candidates) {
            Set<String> candidateTitleTokens = tokenizeKeywords(candidate.getTitle());
            Set<String> candidateTagTokens = candidateTagMap.getOrDefault(candidate.getPostId(), List.of()).stream()
                    .map(this::normalizeKeyword)
                    .filter(token -> !token.isBlank())
                    .collect(Collectors.toSet());

            Set<String> titleMatches = new HashSet<>(candidateTitleTokens);
            titleMatches.retainAll(sourceAllTokens);

            Set<String> tagMatches = new HashSet<>(candidateTagTokens);
            tagMatches.retainAll(sourceAllTokens);

            int score = 0;
            score += titleMatches.size() * 5;
            score += tagMatches.size() * 4;
            if (candidate.getType() == sourcePost.getType()) {
                score += 2;
            }
            score += calculateRecencyBonus(candidate.getCreatedAt());

            if (score <= 0) {
                continue;
            }

            List<String> reasonKeywords = new ArrayList<>();
            reasonKeywords.addAll(titleMatches);
            for (String tagMatch : tagMatches) {
                if (!reasonKeywords.contains(tagMatch)) {
                    reasonKeywords.add(tagMatch);
                }
            }
            if (reasonKeywords.size() > 5) {
                reasonKeywords = reasonKeywords.subList(0, 5);
            }

            scored.add(MarketPostSimilarResponse.builder()
                    .postId(candidate.getPostId())
                    .type(candidate.getType().name())
                    .title(candidate.getTitle())
                    .price(candidate.getPrice())
                    .thumbnailUrl(getThumbnailUrl(candidate.getPostId()))
                    .createdAt(candidate.getCreatedAt())
                    .similarityScore(score)
                    .reasonKeywords(reasonKeywords)
                    .build());
        }

        return scored.stream()
                .sorted(Comparator.comparingInt(MarketPostSimilarResponse::getSimilarityScore).reversed()
                        .thenComparing(MarketPostSimilarResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(safeLimit)
                .toList();
    }

    // =========================================================================
    // ===== 공통 헬퍼 메서드 =====
    // =========================================================================

    private MarketPost findPostOrThrow(Long postId) {
        MarketPost post = marketPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        if (post.getStatus() == MarketPostStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return post;
    }

    private void validateOwner(MarketPost post, Long userId, String action) {
        if (!post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, action + " 권한이 없습니다.");
        }
    }

    private MarketPostType parseMarketPostType(String type) {
        try {
            return MarketPostType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 type 값입니다.");
        }
    }

    private ProductCondition parseProductCondition(String productCondition) {
        return switch (productCondition) {
            case "미개봉", "UNOPENED", "NEW" -> ProductCondition.NEW;
            case "개봉", "OPENED", "LIKE_NEW", "USED", "JUNK" -> ProductCondition.USED;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 productCondition 값입니다.");
        };
    }

    private MarketSpecialType parseSpecialType(String specialType) {
        try {
            return MarketSpecialType.fromInput(specialType);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 specialType 값입니다.");
        }
    }

    private String parseDeliveryMethod(String deliveryMethod) {
        return DeliveryMethod.fromInput(deliveryMethod).getLabel();
    }

    private String toClientCondition(ProductCondition productCondition) {
        return productCondition == ProductCondition.NEW ? "미개봉" : "개봉";
    }

    private Sort buildSort(String sort) {
        return switch (sort != null ? sort : "latest") {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "popular" -> Sort.by("viewCount").descending();
            default -> Sort.by("createdAt").descending();
        };
    }

    private void validateCategoryId(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        if (!marketCategoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 categoryId입니다.");
        }
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

    private List<MarketPostDetailResponse.ImageResponse> getImageResponses(Long postId) {
        return marketPostImageRepository.findByPostIdOrderBySortOrder(postId).stream()
                .map(img -> {
                    String url = mediaFileRepository.findById(img.getMediaId())
                            .map(MediaFile::getUrl).orElse(null);
                    return MarketPostDetailResponse.ImageResponse.builder()
                            .mediaId(img.getMediaId())
                            .imageUrl(url)
                            .sortOrder(img.getSortOrder())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<String> getTagNames(Long postId) {
        return marketPostTagRepository.findByPostId(postId).stream()
                .map(pt -> tagRepository.findById(pt.getTagId()).map(Tag::getName).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<Long, List<String>> getTagNamesByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> tagNameById = tagRepository.findAllById(
                        marketPostTagRepository.findByPostIdIn(postIds).stream()
                                .map(MarketPostTag::getTagId)
                                .collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(Tag::getTagId, Tag::getName));

        Map<Long, List<String>> result = new HashMap<>();
        for (MarketPostTag relation : marketPostTagRepository.findByPostIdIn(postIds)) {
            String tagName = tagNameById.get(relation.getTagId());
            if (tagName == null) continue;
            result.computeIfAbsent(relation.getPostId(), k -> new ArrayList<>()).add(tagName);
        }
        return result;
    }

    private Set<String> tokenizeKeywords(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }

        Set<String> stopWords = Set.of(
                "판매", "팝니다", "구해요", "삽니다", "거래", "상품", "굿즈", "정품", "새제품", "중고"
        );

        return Arrays.stream(normalizeKeyword(text).split("[^\\p{L}\\p{N}]+"))
                .map(String::trim)
                .filter(token -> token.length() >= 2)
                .filter(token -> !stopWords.contains(token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeKeyword(String value) {
        return value == null ? "" : value.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private int calculateRecencyBonus(LocalDateTime createdAt) {
        if (createdAt == null) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (createdAt.isAfter(now.minusDays(1))) return 3;
        if (createdAt.isAfter(now.minusDays(7))) return 2;
        if (createdAt.isAfter(now.minusDays(30))) return 1;
        return 0;
    }

    private MarketPostDetailResponse.SellerResponse getSellerInfo(MarketPost post) {
        User user = post.getUser();
        if (user == null) {
            user = userRepository.findById(post.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        }
        Long userId = user.getUserId();
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        int dealCount = dealRepository.countBySellerIdOrBuyerId(userId, userId);
        int reviewCount = dealReviewRepository.countByRevieweeId(userId);

        return MarketPostDetailResponse.SellerResponse.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .profileImageUrl(profile != null ? profile.getProfileImageUrl() : null)
                .bio(profile != null ? profile.getBio() : null)
                .dealCount(dealCount)
                .reviewCount(reviewCount)
                .build();
    }

    private MarketPostListResponse toListResponse(MarketPost post) {
        return MarketPostListResponse.builder()
                .postId(post.getPostId())
                .type(post.getType().name())
                .title(post.getTitle())
                .price(post.getPrice())
                .productCondition(toClientCondition(post.getProductCondition()))
                .specialType(post.getSpecialType().name())
                .status(post.getStatus().name())
                .thumbnailUrl(getThumbnailUrl(post.getPostId()))
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .favoriteCount(marketPostFavoriteRepository.countByPostId(post.getPostId()))
                .build();
    }

    private MarketPostSimpleResponse toSimpleResponse(MarketPost post) {
        return MarketPostSimpleResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .price(post.getPrice())
                .thumbnailUrl(getThumbnailUrl(post.getPostId()))
                .type(post.getType().name())
                .specialType(post.getSpecialType().name())
                .build();
    }

    private Map<String, Object> buildPageResult(List<?> content, Page<?> page) {
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalElements", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return result;
    }
}
