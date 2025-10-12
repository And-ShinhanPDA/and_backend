package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.CLOSE_PRICE)
@Component
public class ClosePriceEvaluator extends BaseRedisEvaluator {

    public ClosePriceEvaluator(RedisTemplate<String, Object> redisTemplate,
                               AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var daily = getDaily(stockCode);
        if (daily == null) return false;

        Double close = d(daily.get("close"));
        if (close == null) close = d(daily.get("prev_close"));

        log.info("[CLOSE_PRICE] stock={} 종가={}", stockCode, close);
        return false;
    }

    public Double getClose(String stockCode) {
        var daily = getDaily(stockCode);
        if (daily == null) return null;
        Double close = d(daily.get("close"));
        if (close == null) close = d(daily.get("prev_close"));
        return close;
    }
}

