package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.GOLDEN_CROSS)
@Component
public class SMA50200UpEvaluator extends BaseRedisEvaluator {

    public SMA50200UpEvaluator(RedisTemplate<String, Object> redisTemplate,
                             AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var daily = getDaily(stockCode);
        if (daily == null) return false;

        Double sma50 = d(daily.get("sma50"));
        Double sma200 = d(daily.get("sma200"));
        if (sma50 == null || sma200 == null) return false;

        boolean ok = sma50 >= sma200;
        log.info("[GOLDEN_CROSS] alertId={} stock={} sma50={} sma200={} → {}",
                alertId, stockCode, String.format("%.2f", sma50), String.format("%.2f", sma200), ok ? "충족" : "미충족");
        return ok;
    }
}
