package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.SMA_20_UP)
@Component
public class SMA20UpEvaluator  implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double sma20 = metrics.get("sma20");
        Double threshold = manager.getThreshold();
        if (sma20 == null || threshold == null) return false;

        boolean ok = sma20 <= threshold;
        log.info("[SMA_20_UP] alertId={} stock={} sma20={} threshold={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", sma20), String.format("%.2f", threshold), ok ? "충족" : "미충족");
        return ok;
    }
}
