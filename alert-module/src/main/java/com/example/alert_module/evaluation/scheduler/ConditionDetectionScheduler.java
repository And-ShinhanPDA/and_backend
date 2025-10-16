package com.example.alert_module.evaluation.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConditionDetectionScheduler {

    @Scheduled(cron = "0 * * * * *")
    public void runConditionDetection() {
        log.info("🧭 [ConditionDetectionScheduler] 조건 탐색 스케줄 시작!");
    }
}
