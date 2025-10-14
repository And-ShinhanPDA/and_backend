package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ConditionTypeMapping(ConditionType.SMA_5_DOWN)
public class SMA5DownEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double sma5 = metrics.get("sma5");
        Double threshold = manager.getThreshold();

        if (sma5 == null || threshold == null) return false;

        // "DOWN" 조건 → 현재 sma5 <= threshold
        boolean ok = sma5 <= threshold;

        log.info("[SMA_5_DOWN] userId={} stock={} sma5={} threshold={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", sma5),
                String.format("%.2f", threshold),
                ok ? "충족" : "미충족");

        return ok;
    }
}