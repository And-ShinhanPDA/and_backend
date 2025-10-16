package com.example.alert_module.evaluation.service;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluatorManager;
import com.example.alert_module.notification.event.AlertEventPublisher;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import com.example.alert_module.management.repository.AlertRepository;
import java.util.stream.Collectors;
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

    @Transactional(readOnly = true)
    public void detectForStock(String stockCode) {
        List<Alert> activeAlerts = alertRepository.findByIsActivedAndStockCode(true, stockCode);
        log.info("🔹 [{}] alert 개수 = {}", stockCode, activeAlerts.size());
        if (activeAlerts.isEmpty()) {
            log.debug("⚪ [{}] 활성 알림 없음", stockCode);
            return;
        }

        for (Alert alert : activeAlerts) {
            List<AlertConditionManager> managers = managerRepo.findByAlertId(alert.getId());
            log.info("🔹 [{}] managers 개수 = {}", stockCode, managers.size());
            for (AlertConditionManager manager : managers) {
                // 🔸 data_scope별로 맞는 Redis 데이터 로드
                Map<String, Double> metrics = evaluatorManager.loadMetrics(manager);
                log.info("🔹 [{}] metrics = {}", stockCode, metrics);
                if (evaluatorManager.evaluate(manager, metrics)) {
                    eventPublisher.publish(manager);
                }
            }
        }
    }
}