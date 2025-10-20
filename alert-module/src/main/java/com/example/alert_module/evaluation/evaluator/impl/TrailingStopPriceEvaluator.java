package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.TRAILING_STOP_PRICE)
@Component
public class TrailingStopPriceEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double price = metrics.get("price");       // 현재가
        Double recentHigh = manager.getThreshold2(); // 최근 고가
        Double dropTarget = manager.getThreshold();  // 손절 기준 금액

        if (price == null || recentHigh == null || dropTarget == null) return false;

        // 조건: 최근 고가 - 현재가 ≥ 설정 금액 → 최근 고점 대비 손절폭 초과
        boolean ok = (recentHigh - price) >= dropTarget;
        log.info("[TRAILING_STOP_PRICE] alertId={} stock={} price={} recentHigh={} dropTarget={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", price),
                String.format("%.2f", recentHigh),
                String.format("%.2f", dropTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
