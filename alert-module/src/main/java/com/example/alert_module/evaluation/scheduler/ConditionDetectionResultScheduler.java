package com.example.alert_module.evaluation.scheduler;

import com.example.alert_module.evaluation.entity.ConditionSearchResult;
import com.example.alert_module.evaluation.evaluator.service.AlertEvaluationService;
import com.example.alert_module.evaluation.repository.ConditionSearchResultRepository;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConditionDetectionResultScheduler {

    private final AlertRepository alertRepository;
    private final AlertEvaluationService alertEvaluationService;
    private final ConditionSearchResultRepository conditionSearchResultRepository;

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void runConditionDetectionResult() {
        log.info("🧭 [ConditionDetectionResultScheduler] 조건 탐색 스케줄 시작!");

        // 1️⃣ 조건형 알림 전체 조회
        List<Alert> conditionAlerts = alertRepository.findConditionAlerts();
        if (conditionAlerts.isEmpty()) {
            log.info("⚠️ 조건 탐색 대상 알림이 없습니다.");
            return;
        }

        // 2️⃣ 각 알림별 감시 중인 종목 탐색
        for (Alert alert : conditionAlerts) {
            List<ConditionSearchResult> results = conditionSearchResultRepository.findByAlert_Id(alert.getId());
            if (results.isEmpty()) continue;

            for (ConditionSearchResult result : results) {
                boolean detected = alertEvaluationService.evaluateAlertForCondition(alert, result.getStockCode());
                boolean before = Boolean.TRUE.equals(result.getIsTriggered());
                boolean after = detected;

                // 상태 변화 없으면 패스
                if (before == after) continue;

                // 3️⃣ 상태 변경
                result.setIsTriggered(after);
                result.setTriggerDate(after ? LocalDateTime.now() : null);

                // 4️⃣ 저장 (update)
                conditionSearchResultRepository.save(result);

                if (after) {
                    log.info("🚨 [조건 충족] alertId={}, stockCode={} → 트리거 ON", alert.getId(), result.getStockCode());
                } else {
                    log.info("🕊️ [조건 해제] alertId={}, stockCode={} → 트리거 OFF", alert.getId(), result.getStockCode());
                }
            }
        }

        log.info("✅ [ConditionDetectionResultScheduler] 조건 탐색 스케줄 완료");
    }
}
