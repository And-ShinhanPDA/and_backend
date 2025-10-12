package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_RATE_DAILY_UP)
@Component
public class PriceRateDailyUpEvaluator extends BaseRedisEvaluator {

    public PriceRateDailyUpEvaluator(RedisTemplate<String, Object> redisTemplate,
                                      AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var minute = getMinute(stockCode);
        if (minute == null) return false;

        Double diffFromOpenPct = d(minute.get("diffFromOpenPct")); // 시가 대비 등락률(%)
        Double threshold = manager.getThreshold(); // 설정 백분율
        if (diffFromOpenPct == null || threshold == null) return false;

        // 상승 기준: 시가 대비 등락률 ≥ 설정 백분율
        boolean ok = diffFromOpenPct >= threshold;
        log.info("[PRICE_RATE_DAILY_UP] alertId={} stock={} diffFromOpenPct={} threshold={} → {}",
                alertId, stockCode,
                String.format("%.2f", diffFromOpenPct), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");
        return ok;
    }

}
