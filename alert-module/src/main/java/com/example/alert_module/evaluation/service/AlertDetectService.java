package com.example.alert_module.evaluation.service;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluatorManager;
import com.example.alert_module.evaluation.evaluator.service.AlertEvaluationService;
import com.example.alert_module.notification.event.AlertEventPublisher;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import com.example.alert_module.management.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final AlertEvaluationService alertEvaluationService;

    @Transactional(readOnly = true)
    public void detectForStock(String stockCode) {
        // 1️⃣ 해당 종목의 활성화된 Alert만 조회
        List<Alert> activeAlerts = alertRepository.findByIsActivedAndStockCode(true, stockCode);
        log.info("🔹 [{}] alert 개수 = {}", stockCode, activeAlerts.size());
        if (activeAlerts.isEmpty()) {
            log.debug("⚪ [{}] 활성 알림 없음", stockCode);
            return;
        }

        // 2️⃣ 각 Alert별 조건 평가
        for (Alert alert : activeAlerts) {
            if (alertEvaluationService.evaluateAlert(alert.getId())) {
                eventPublisher.publish(alert);
            }
        }
    }
}

