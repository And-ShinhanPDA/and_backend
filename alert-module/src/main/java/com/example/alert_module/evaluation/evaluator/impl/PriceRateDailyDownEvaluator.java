package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_RATE_DAILY_DOWN)
@Component
public class PriceRateDailyDownEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double diffFromOpenPct = metrics.get("diffFromOpenPct"); // 시가 대비 등락률(%)
        Double threshold = manager.getThreshold(); // 설정 백분율
        if (diffFromOpenPct == null || threshold == null) return false;

        // 상승 기준: 시가 대비 등락률 <= 설정 백분율
        boolean ok = diffFromOpenPct <= threshold;
        log.info("[PRICE_RATE_DAILY_DOWN] alertId={} stock={} diffFromOpenPct={} threshold={} → {}",
                manager.getAlert().getId(), manager.getAlert().getStockCode(),
                String.format("%.2f", diffFromOpenPct), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");
        return ok;
    }
}
