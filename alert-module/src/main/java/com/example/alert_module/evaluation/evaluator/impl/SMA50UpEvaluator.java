package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.SMA_50_UP)
@Component
public class SMA50UpEvaluator extends BaseRedisEvaluator {

    public SMA50UpEvaluator(RedisTemplate<String, Object> redisTemplate,
                            AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var daily = getDaily(stockCode);
        if (daily == null) return false;

        Double sma50 = d(daily.get("sma50"));
        Double threshold = manager.getThreshold();
        if (sma50 == null || threshold == null) return false;

        boolean ok = sma50 <= threshold;
        log.info("[SMA_50_UP] alertId={} stock={} sma50={} threshold={} → {}",
                alertId, stockCode, String.format("%.2f", sma50), String.format("%.2f", threshold), ok ? "충족" : "미충족");
        return ok;
    }
}
