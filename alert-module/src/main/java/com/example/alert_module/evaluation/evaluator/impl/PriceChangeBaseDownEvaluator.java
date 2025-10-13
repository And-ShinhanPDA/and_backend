package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_CHANGE_BASE_DOWN)
@Component
public class PriceChangeBaseDownEvaluator extends BaseRedisEvaluator {

    public PriceChangeBaseDownEvaluator(RedisTemplate<String, Object> redisTemplate,
                                      AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var minute = getMinute(stockCode);
        if (minute == null) return false;

        Double price = d(minute.get("price"));
        Double basePrice = manager.getThreshold2();
        Double diffTarget = manager.getThreshold();

        if (price == null || basePrice == null || diffTarget == null) return false;

        boolean ok = price <= basePrice - diffTarget;
        log.info("[PRICE_CHANGE_BASE_DOWN] alertId={} stock={} price={} basePrice={} diffTarget={} → {}",
                alertId, stockCode,
                String.format("%.2f", price),
                String.format("%.2f", basePrice),
                String.format("%.2f", diffTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
