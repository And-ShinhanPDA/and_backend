package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_CHANGE_DAILY_UP)
@Component
public class PriceChangeDailyUpEvaluator extends BaseRedisEvaluator {

    public PriceChangeDailyUpEvaluator(RedisTemplate<String, Object> redisTemplate,
                                         AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var minute = getMinute(stockCode);
        if (minute == null) return false;

        Double diffFromOpen = d(minute.get("diffFromOpen"));
        Double threshold = manager.getThreshold();
        if (diffFromOpen == null || threshold == null) return false;

        boolean ok = diffFromOpen >= threshold;
        log.info("[PRICE_CHANGE_DAILY_UP] alertId={} stock={} diffFromOpen={} threshold={} → {}",
                alertId, stockCode,
                String.format("%.2f", diffFromOpen), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");
        return ok;
    }
}
