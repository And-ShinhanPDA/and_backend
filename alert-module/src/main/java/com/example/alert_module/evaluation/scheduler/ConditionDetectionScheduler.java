package com.example.alert_module.evaluation.scheduler;

import com.example.alert_module.evaluation.evaluator.service.AlertEvaluationService;
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
    private final AlertEvaluationService alertEvaluationService;

    @Scheduled(cron = "0 * * * * *")
    public void runConditionDetection() {
        log.info("🧭 [ConditionDetectionScheduler] 조건 탐색 스케줄 시작!");

        List<Alert> conditionAlerts = alertRepository.findConditionAlerts();

        if (conditionAlerts.isEmpty()) {
            log.info("⚪ 조건 탐지용 알림 없음.");
            return;
        }

        log.info("🔍 조건 탐지용 알림 수: {}", conditionAlerts.size());
        conditionAlerts.forEach(alert -> {
            List<String> matched = alertEvaluationService.evaluateConditionAlert(alert.getId());
            if (!matched.isEmpty()) {
                log.info("🚨 [조건 충족 알림 발생] alertId={} 충족기업={}", alert.getId(), matched);
                // TODO: 이후 FCM or MQ 전송 로직 추가
            }
        });
    }
}
