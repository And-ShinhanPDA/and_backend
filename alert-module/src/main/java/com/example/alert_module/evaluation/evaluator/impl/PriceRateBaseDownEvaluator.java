package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_RATE_BASE_DOWN)
@Component
public class PriceRateBaseDownEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double price = metrics.get("price");
        Double basePrice = manager.getThreshold2();
        Double pctTarget = manager.getThreshold();   // 설정 백분율
        if (price == null || basePrice == null || pctTarget == null) return false;

        // 상승률 = (현재가 - 기준가) / 기준가 * 100
        double rate = ((price - basePrice) / basePrice) * 100.0;

        // 상승 조건: 상승률 ≥ 설정 백분율
        boolean ok = rate <= pctTarget;
        log.info("[PRICE_RATE_BASE_DOWN] alertId={} stock={} price={} basePrice={} rate={} threshold={} → {}",
                manager.getAlert().getId(), manager.getAlert().getStockCode(),
                String.format("%.2f", price),
                String.format("%.2f", basePrice),
                String.format("%.2f", rate),
                String.format("%.2f", pctTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
