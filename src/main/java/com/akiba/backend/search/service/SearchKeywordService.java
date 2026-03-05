package com.akiba.backend.search.service;

import com.akiba.backend.market.dto.response.PopularKeywordResponse;
import com.akiba.backend.search.domain.SearchKeyword;
import com.akiba.backend.search.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordKeyword(String rawKeyword) {
        String keyword = normalize(rawKeyword);
        if (keyword.isEmpty()) {
            return;
        }

        SearchKeyword searchKeyword = searchKeywordRepository.findByKeyword(keyword)
                .orElseGet(() -> SearchKeyword.builder().keyword(keyword).build());

        if (searchKeyword.getId() == null) {
            searchKeywordRepository.save(searchKeyword);
            return;
        }

        searchKeyword.increment();
    }

    public PopularKeywordResponse getPopularKeywords(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 10);
        List<SearchKeyword> topKeywords = searchKeywordRepository
                .findAllByOrderBySearchCountDescLastSearchedAtDesc(PageRequest.of(0, safeLimit));

        List<PopularKeywordResponse.KeywordItem> keywords = IntStream.range(0, topKeywords.size())
                .mapToObj(i -> PopularKeywordResponse.KeywordItem.builder()
                        .rank(i + 1)
                        .keyword(topKeywords.get(i).getKeyword())
                        .trend("SAME")
                        .build())
                .toList();

        return PopularKeywordResponse.builder()
                .updatedAt("오후 " + LocalTime.now().getHour() + "시 업데이트")
                .keywords(keywords)
                .build();
    }

    @Transactional
    public Map<String, Long> cleanupKeywords() {
        LocalDateTime now = LocalDateTime.now();
        long removedLowQuality = searchKeywordRepository
                .deleteBySearchCountLessThanEqualAndLastSearchedAtBefore(2, now.minusDays(7));
        long removedExpired = searchKeywordRepository
                .deleteByLastSearchedAtBefore(now.minusDays(14));

        return Map.of(
                "removedLowQuality", removedLowQuality,
                "removedExpired", removedExpired,
                "removedTotal", removedLowQuality + removedExpired
        );
    }

    private String normalize(String rawKeyword) {
        if (rawKeyword == null) {
            return "";
        }
        return rawKeyword.trim().replaceAll("\\s+", " ");
    }
}
