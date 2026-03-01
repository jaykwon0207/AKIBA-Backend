package com.akiba.backend.search.scheduler;

import com.akiba.backend.search.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchKeywordCleanupScheduler {

    private final SearchKeywordService searchKeywordService;

    // 매일 03:30에 인기검색어 정리 실행
    @Scheduled(cron = "0 30 3 * * *")
    public void cleanupSearchKeywords() {
        try {
            Map<String, Long> result = searchKeywordService.cleanupKeywords();
            log.info("검색어 정리 완료: {}", result);
        } catch (Exception e) {
            log.error("검색어 정리 중 오류 발생", e);
        }
    }
}
