//package com.example.alert_module.evaluation.scheduler;
//
//import com.example.alert_module.evaluation.entity.ConditionSearch;
//import com.example.alert_module.evaluation.evaluator.service.AlertEvaluationService;
//import com.example.alert_module.evaluation.repository.ConditionSearchRepository;
//import com.example.alert_module.management.entity.Alert;
//import com.example.alert_module.management.repository.AlertRepository;
//import jakarta.transaction.Transactional;
//import java.time.LocalDateTime;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ConditionDetectionScheduler {
//
//    private final AlertRepository alertRepository;
//    private final AlertEvaluationService alertEvaluationService;
//    private final ConditionSearchRepository conditionSearchRepository;
//
//    @Transactional
//    @Scheduled(cron = "0 * * * * *")
//    public void runConditionDetection() {
//        log.info("🧭 [ConditionDetectionScheduler] 조건 탐색 스케줄 시작!");
//
//        List<Alert> conditionAlerts = alertRepository.findConditionAlerts();
//
//        if (conditionAlerts.isEmpty()) {
//            log.info("⚪ 조건 탐지용 알림 없음.");
//            return;
//        }
//
//        log.info("🔍 조건 탐지용 알림 수: {}", conditionAlerts.size());
//
//        for (Alert alert : conditionAlerts) {
//            List<ConditionSearch> conditionList = conditionSearchRepository.findByAlert_Id(alert.getId());
//            if (conditionList.isEmpty()) continue;
//
//            for (ConditionSearch cs : conditionList) {
//                boolean detected = alertEvaluationService.evaluateAlertForCondition(alert, cs.getStockCode());
//                boolean before = cs.getIsTriggered() != null && cs.getIsTriggered();
//                boolean after = detected;
//
//                // 변화가 없으면 skip
//                if (before == after) continue;
//
//                // 변화 발생 (false→true 또는 true→false)
//                cs.setIsTriggered(after);
//                if (after) {
//                    cs.setTriggerDate(LocalDateTime.now());
//                    log.info("🚨 [조건 충족] alertId={}, stockCode={} → 트리거 ON", alert.getId(), cs.getStockCode());
//                    // TODO: FCM or MQ 알림 발송 로직
//                } else {
//                    log.info("🕊️ [조건 해제] alertId={}, stockCode={} → 트리거 OFF", alert.getId(), cs.getStockCode());
//                    // TODO: 필요 시 “조건 해제” 알림 발송
//                }
//
//                conditionSearchRepository.save(cs);
//            }
//        }
//
//        log.info("✅ [ConditionDetectionScheduler] 조건 탐색 스케줄 완료");
//    }
//}
