package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.BOLLINGER_LOWER_TOUCH)
@Component
public class BollingerLowerEvaluator implements ConditionEvaluator {


    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double bbLower = metrics.get("bbLower");
        Double threshold = manager.getThreshold();

        if (bbLower == null || threshold == null) return false;

        boolean ok = bbLower >= threshold;
        log.info("[BOLLINGER_LOWER_TOUCH] alertId={} stock={} bbLower={} threshold={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", bbLower), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");

        return ok;
    }

}
