package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.RSI_OVER)
@Component
public class RsiOverEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double rsi = metrics.get("rsi14"); // 일별 rsi값
        Double threshold = manager.getThreshold(); // 설정값
        if (rsi == null || threshold == null) return false;

        boolean ok = rsi <= threshold;
        log.info("[RSI_OVER] alertId={} stock={} rsi={} threshold={} → {}",
                manager.getAlert().getId(), manager.getAlert().getStockCode(),
                String.format("%.2f", rsi), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");

        return ok;
    }
}
