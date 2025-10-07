package com.example.data_process_module.scheduler;

import com.example.data_process_module.persist.service.PersistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyIndicatorScheduler {

    private final PersistService persistService;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
//    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void syncDailyIndicators() {
        log.info("📦 [SCHEDULER] Redis → MySQL 일별 데이터 동기화 시작");
        persistService.syncDailyDataToDB();
        log.info("✅ [SCHEDULER] Redis → MySQL 일별 데이터 동기화 완료");
    }
}
