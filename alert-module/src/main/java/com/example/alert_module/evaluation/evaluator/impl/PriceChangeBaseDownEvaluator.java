package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_CHANGE_BASE_DOWN)
@Component
public class PriceChangeBaseDownEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double price = metrics.get("price");
        Double basePrice = manager.getThreshold2();
        Double diffTarget = manager.getThreshold();

        if (price == null || basePrice == null || diffTarget == null) return false;

        boolean ok = price <= basePrice - diffTarget;
        log.info("[PRICE_CHANGE_BASE_DOWN] alertId={} stock={} price={} basePrice={} diffTarget={} → {}",
                manager.getAlert().getId(), manager.getAlert().getStockCode(),
                String.format("%.2f", price),
                String.format("%.2f", basePrice),
                String.format("%.2f", diffTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
