package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.SMA_200_DOWN)
@Component
public class SMA200DownEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double sma200 = metrics.get("sma200");
        Double threshold = manager.getThreshold();
        if (sma200 == null || threshold == null) return false;

        boolean ok = sma200 >= threshold;
        log.info("[SMA_200_DOWN] alertId={} stock={} sma200={} threshold={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", sma200), String.format("%.2f", threshold), ok ? "충족" : "미충족");
        return ok;
    }
}
