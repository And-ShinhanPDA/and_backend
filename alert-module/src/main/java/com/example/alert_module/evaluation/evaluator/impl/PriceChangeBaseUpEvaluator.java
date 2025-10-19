package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.entity.ConditionBase;
import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.evaluation.repository.ConditionBaseRepository;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_CHANGE_BASE_UP)
@RequiredArgsConstructor
@Component
public class PriceChangeBaseUpEvaluator implements ConditionEvaluator {

    private final ConditionBaseRepository conditionBaseRepository;

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double price = metrics.get("price");
        Double diffTarget = manager.getThreshold();

        String stockCode = manager.getAlert().getStockCode();
        Double basePrice;
        if (stockCode == null) {
            stockCode = metrics.get("stockCode").toString();

            if (stockCode.endsWith(".0")) {
                stockCode = stockCode.substring(0, stockCode.length() - 2);
            }

            stockCode = stockCode.replaceAll("[^0-9]", "");

            if (stockCode.length() < 6) {
                stockCode = String.format("%06d", Integer.parseInt(stockCode));
            }

            ConditionBase conditionBase =
                    conditionBaseRepository.findByAlertIdAndStockCode(manager.getAlert().getId(), stockCode);
            if (conditionBase == null) {
                log.warn("[PRICE_RATE_BASE_DOWN] 기준가 미등록 → 평가 불가 (alertId={}, stockCode={})",
                        manager.getAlert().getId(), stockCode);
                return false;
            }
            basePrice = conditionBase.getBaseValue();
        } else {
            basePrice = manager.getThreshold2();
        }

        if (price == null || basePrice == null || diffTarget == null) return false;

        boolean ok = price >= basePrice + diffTarget;
        log.info("[PRICE_CHANGE_BASE_UP] alertId={} stock={} price={} basePrice={} diffTarget={} → {}",
                manager.getAlert().getId(), manager.getAlert().getStockCode(),
                String.format("%.2f", price),
                String.format("%.2f", basePrice),
                String.format("%.2f", diffTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
