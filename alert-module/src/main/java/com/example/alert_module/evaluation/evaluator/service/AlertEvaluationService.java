package com.example.alert_module.evaluation.evaluator.service;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluatorManager;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEvaluationService {

    private final AlertRepository alertRepository;
    private final ConditionEvaluatorManager evaluatorManager;

    /**
     * 특정 알림(alertId)에 대해 전체 조건 평가 수행
     * - 같은 카테고리는 OR
     * - 다른 카테고리는 AND
     */
    @Transactional
    public boolean evaluateAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));

        String stockCode = alert.getStockCode();
        log.info("🚀 [AlertEvaluationService] 평가 시작 - alertId={}, stock={}", alertId, stockCode);

        // 1️⃣ AlertConditionManager 목록 가져오기
        List<AlertConditionManager> managers = alert.getConditionManagers();
        if (managers.isEmpty()) {
            log.warn("⚠️ 조건이 없는 알림: {}", alertId);
            return false;
        }

        // 2️⃣ 카테고리별 그룹화
        Map<String, List<AlertConditionManager>> grouped = managers.stream()
                .collect(Collectors.groupingBy(m -> m.getAlertCondition().getCategory()));

        boolean overall = true;

        // 3️⃣ 카테고리별 OR 판별
        for (Map.Entry<String, List<AlertConditionManager>> entry : grouped.entrySet()) {
            String category = entry.getKey();
            List<AlertConditionManager> list = entry.getValue();

            boolean categoryResult = false;

            for (AlertConditionManager manager : list) {
                Map<String, Double> metrics = evaluatorManager.loadMetrics(manager);
                boolean condResult = evaluatorManager.evaluate(manager, metrics);
                categoryResult |= condResult;
            }

            log.info("📁 [{}] 카테고리 결과: {}", category, categoryResult ? "충족" : "미충족");
            overall &= categoryResult;
        }

        log.info("✅ [AlertEvaluationService] 최종 결과 alertId={} → {}", alertId, overall ? "충족" : "미충족");
        return overall;
    }

    @Transactional
    public List<String> evaluateConditionAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));

        log.info("🚀 [AlertEvaluationService] 조건 탐지 평가 시작 - alertId={}", alertId);

        List<AlertConditionManager> managers = alert.getConditionManagers();
        if (managers.isEmpty()) {
            log.warn("⚠️ 조건이 없는 알림: {}", alertId);
            return List.of();
        }

        // 예시로 20개 기업 코드 (실제로는 다른 모듈에서 받아올 수도 있음)
        List<String> stockCodes = List.of(
                "005930",  // 삼성전자
                "000660",  // SK하이닉스
                "373220",  // LG에너지솔루션
                "012450",  // 한화에어로스페이스
                "005380",  // 현대차
                "105560",  // KB금융
                "035420",  // NAVER
                "329180",  // HD현대중공업
                "068270",  // 셀트리온
                "034020",  // 두산에너빌리티
                "000270",  // 기아
                "055550",  // 신한지주
                "035720",  // 카카오
                "086790",  // 하나금융지주
                "015760",  // 한국전력
                "005490",  // POSCO홀딩스
                "011200",  // HMM
                "138040",  // 메리츠금융지주
                "316140",  // 우리금융지주
                "010130"   // 고려아연
        );

        List<String> matchedStocks = stockCodes.stream()
                .filter(stockCode -> evaluateAlertForCondition(alert, stockCode))
                .toList();

        log.info("✅ [AlertEvaluationService] 조건 탐지 완료 - 충족 기업 수: {}", matchedStocks.size());
        matchedStocks.forEach(code -> log.info("🎯 조건 충족 종목: {}", code));

        return matchedStocks;
    }

    /**
     * 내부적으로 특정 종목 기준 조건 탐색 수행
     */
    private boolean evaluateAlertForCondition(Alert alert, String stockCode) {
        List<AlertConditionManager> managers = alert.getConditionManagers();

        // 카테고리별 그룹화
        Map<String, List<AlertConditionManager>> grouped = managers.stream()
                .collect(Collectors.groupingBy(m -> m.getAlertCondition().getCategory()));

        boolean overall = true;

        for (Map.Entry<String, List<AlertConditionManager>> entry : grouped.entrySet()) {
            String category = entry.getKey();
            List<AlertConditionManager> list = entry.getValue();

            boolean categoryResult = false;

            for (AlertConditionManager manager : list) {
                Map<String, Double> metrics = evaluatorManager.loadMetricsForStock(manager, stockCode);
                boolean condResult = evaluatorManager.evaluate(manager, metrics);
                categoryResult |= condResult;
            }

            log.debug("📁 [{}] {} → {}", category, stockCode, categoryResult ? "충족" : "미충족");
            overall &= categoryResult;
        }

        return overall;
    }

}
