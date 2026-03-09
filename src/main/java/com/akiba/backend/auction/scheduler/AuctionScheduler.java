// ========================================================================
// 파일 경로: com/akiba/backend/auction/scheduler/AuctionScheduler.java
// 설명: 경매 종료 시간이 지난 경매를 자동으로 처리하는 스케줄러
//
// [동작 방식]
// - 1분마다 실행 (cron = "0 * * * * *")
// - 종료 시간이 지난 ACTIVE 경매를 찾아서
//   → 입찰 있으면: 최고 입찰자 낙찰 (SOLD)
//   → 입찰 없으면: 유찰 (CLOSED)
//
// [사용 조건]
// - main 클래스에 @EnableScheduling 추가 필요
//   @SpringBootApplication
//   @EnableScheduling    ← 이거 추가
//   public class BackendApplication { ... }
// ========================================================================
package com.akiba.backend.auction.scheduler;

import com.akiba.backend.auction.service.AuctionPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionPostService auctionPostService;

    /**
     * 1분마다 종료된 경매를 처리
     * cron = "초 분 시 일 월 요일"
     * "0 * * * * *" = 매분 0초에 실행
     */
    @Scheduled(cron = "0 * * * * *")
    public void processEndedAuctions() {
        try {
            auctionPostService.processEndedAuctions();
            log.info("경매 종료 처리 스케줄러 실행 완료");
        } catch (Exception e) {
            log.error("경매 종료 처리 중 오류 발생", e);
        }
    }
}
