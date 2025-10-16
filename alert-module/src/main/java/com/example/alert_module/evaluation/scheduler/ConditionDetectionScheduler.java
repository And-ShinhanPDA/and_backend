package com.example.alert_module.evaluation.scheduler;

import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.repository.AlertRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConditionDetectionScheduler {

    private final AlertRepository alertRepository;

    @Scheduled(cron = "0 * * * * *")
    public void runConditionDetection() {
        log.info("🧭 [ConditionDetectionScheduler] 조건 탐색 스케줄 시작!");

        List<Alert> conditionAlerts = alertRepository.findConditionAlerts();

        if (conditionAlerts.isEmpty()) {
            log.info("⚪ 조건 탐지용 알림 없음.");
            return;
        }

        log.info("🔍 조건 탐지용 알림 수: {}", conditionAlerts.size());
        conditionAlerts.forEach(alert ->
                log.info("📌 [조건탐지 알림] id={} | title='{}' | user={}",
                        alert.getId(), alert.getTitle(), alert.getUserId())
        );
    }
}
