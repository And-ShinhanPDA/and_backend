package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.TRAILING_BUY_PRICE)
@Component
public class TrailingBuyPriceEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double price = metrics.get("price");       // 현재가
        Double recentHigh = manager.getThreshold2(); // 최근 고가
        Double riseTarget = manager.getThreshold();  // 상승 기준 금액

        if (price == null || recentHigh == null || riseTarget == null) return false;

        // ✅ 조건: 현재가 - 최근 고가 ≥ 설정 금액 → 고점 돌파 시 매수 트리거
        boolean ok = (price - recentHigh) >= riseTarget;
        log.info("[TRAILING_BUY_PRICE] alertId={} stock={} price={} recentHigh={} riseTarget={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", price),
                String.format("%.2f", recentHigh),
                String.format("%.2f", riseTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
