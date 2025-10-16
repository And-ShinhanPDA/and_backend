package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_CHANGE_DAILY_DOWN)
@Component
public class PriceChangeDailyDownEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double diffFromOpen = metrics.get("diffFromOpen");
        Double threshold = manager.getThreshold();
        if (diffFromOpen == null || threshold == null) return false;

        boolean ok = diffFromOpen <= -threshold;
        log.info("[PRICE_CHANGE_DAILY_DOWN] alertId={} stock={} diffFromOpen={} threshold={} → {}",
                manager.getAlert().getId(), manager.getAlert().getStockCode(),
                String.format("%.2f", diffFromOpen), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");
        return ok;
    }
}
