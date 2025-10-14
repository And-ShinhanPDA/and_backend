package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.SMA_10_UP)
@Component
public class SMA10UpEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double sma10 = metrics.get("sma10");
        Double threshold = manager.getThreshold();
        if (sma10 == null || threshold == null) return false;

        boolean ok = sma10 <= threshold;
        log.info("[SMA_10_UP] alertId={} stock={} sma10={} threshold={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", sma10), String.format("%.2f", threshold), ok ? "충족" : "미충족");
        return ok;
    }
}
