package com.example.alert_module.evaluation.service;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluatorManager;
import com.example.alert_module.evaluation.event.AlertEventPublisher;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import com.example.alert_module.management.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertDetectService {

    private final AlertRepository alertRepository;
    private final AlertConditionManagerRepository managerRepo;
    private final ConditionEvaluatorManager evaluatorManager;
    private final AlertEventPublisher eventPublisher;

    public void detectForStock(String stockCode) {
        // 1️⃣ 해당 종목의 활성화된 Alert만 조회
        List<Alert> activeAlerts = alertRepository.findByIsActivedAndStockCode(true, stockCode);
        if (activeAlerts.isEmpty()) {
            log.debug("⚪ [{}] 활성 알림 없음", stockCode);
            return;
        }

        // 2️⃣ 각 Alert별 조건 평가
        for (Alert alert : activeAlerts) {
            List<AlertConditionManager> managers = managerRepo.findByAlertId(alert.getId());

            for (AlertConditionManager manager : managers) {
                // 🔸 data_scope별로 맞는 Redis 데이터 로드
                Map<String, Double> metrics = evaluatorManager.loadMetrics(manager);

                if (evaluatorManager.evaluate(manager, metrics)) {
                    eventPublisher.publish(manager);
                }
            }
        }
    }
}