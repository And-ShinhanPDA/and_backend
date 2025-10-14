package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.SMA_100_UP)
@Component
public class SMA100UpEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double sma100 = metrics.get("sma100");
        Double threshold = manager.getThreshold();
        if (sma100 == null || threshold == null) return false;

        boolean ok = sma100 <= threshold;
        log.info("[SMA_100_UP] alertId={} stock={} sma100={} threshold={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", sma100), String.format("%.2f", threshold), ok ? "충족" : "미충족");
        return ok;
    }
}
