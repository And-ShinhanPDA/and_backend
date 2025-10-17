//package com.example.alert_module.evaluation.evaluator.service;
//
//import com.example.alert_module.notification.event.AlertEventPublisher;
//import com.example.alert_module.management.entity.Alert;
//import com.example.alert_module.management.repository.AlertRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AlertDetectService {
//
//    private final AlertRepository alertRepository;
//    private final AlertEventPublisher eventPublisher;
//    private final AlertEvaluationService alertEvaluationService;
//
//    @Transactional
//    public void detectForStock(String stockCode) {
//        List<Alert> activeAlerts = alertRepository.findByIsActivedAndStockCode(true, stockCode);
//        log.info("🔹 [{}] 활성 알림 개수 = {}", stockCode, activeAlerts.size());
//
//        // ✅ 조건이 있는 알림만 필터링
//        List<Alert> alertsWithConditions = activeAlerts.stream()
//                .filter(alert -> alert.getConditionManagers() != null && !alert.getConditionManagers().isEmpty())
//                .toList();
//
//        if (alertsWithConditions.isEmpty()) {
//            log.debug("⚪ [{}] 평가할 조건이 있는 알림 없음", stockCode);
//            return;
//        }
//
//        log.info("📊 [{}] 평가 대상 알림 = {}개 (조건 없는 알림 {}개 제외)",
//                stockCode, alertsWithConditions.size(), activeAlerts.size() - alertsWithConditions.size());
//
//        for (Alert alert : alertsWithConditions) {
//            boolean isTriggeredNow = alertEvaluationService.evaluateAlert(alert.getId());
//
//            log.info("원래 상태 - alert : {} , isTriggeredNow : {}",alert.getIsTriggered(), isTriggeredNow);
//
//            if (alert.getIsTriggered() && !isTriggeredNow) {
//                log.info("조건 벗어남.");
//                alert.setIsTriggered(false);
//                eventPublisher.publish(alert);
//            } else if (!alert.getIsTriggered() && isTriggeredNow) {
//                alert.setIsTriggered(true);
//                log.info("조건 만족함.");
//                log.info("🚀 [{}] alertId={} trigger 상태 변경 ({} → true)",
//                        stockCode, alert.getId(),
//                        alert.getIsTriggered() ? "false" : "true");
//                eventPublisher.publish(alert);
//            }
//        }
//    }
//}
//
