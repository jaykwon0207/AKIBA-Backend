package com.akiba.backend.used.repository;

import com.akiba.backend.used.domain.MarketPostTag;
import com.akiba.backend.used.domain.MarketPostTagId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Collection;

public interface MarketPostTagRepository extends JpaRepository<MarketPostTag, MarketPostTagId> {

    interface RecommendedTagProjection {
        String getTagName();
        Long getUseCount();
    }

    // 게시글의 태그 목록 조회
    List<MarketPostTag> findByPostId(Long postId);
    List<MarketPostTag> findByPostIdIn(Collection<Long> postIds);

    // 게시글의 태그 전체 삭제 (수정 시 교체용)
    void deleteByPostId(Long postId);

    @Query(
            value = """
                    SELECT t.name AS tagName, COUNT(*) AS useCount
                    FROM market_post_tags mpt
                    JOIN tags t ON t.tag_id = mpt.tag_id
                    JOIN market_posts mp ON mp.post_id = mpt.post_id
                    WHERE mp.status = 'ACTIVE'
                    GROUP BY t.tag_id, t.name
                    ORDER BY COUNT(*) DESC, t.name ASC
                    """,
            nativeQuery = true
    )
    List<RecommendedTagProjection> findRecommendedTagsForActivePosts(Pageable pageable);

    @Query(
            value = """
                    SELECT t.name AS tagName, COUNT(*) AS useCount
                    FROM market_post_tags mpt
                    JOIN tags t ON t.tag_id = mpt.tag_id
                    JOIN market_posts mp ON mp.post_id = mpt.post_id
                    WHERE mp.status = 'ACTIVE'
                      AND mp.type = :type
                    GROUP BY t.tag_id, t.name
                    ORDER BY COUNT(*) DESC, t.name ASC
                    """,
            nativeQuery = true
    )
    List<RecommendedTagProjection> findRecommendedTagsForActivePostsByType(
            @Param("type") String type,
            Pageable pageable
    );

}
