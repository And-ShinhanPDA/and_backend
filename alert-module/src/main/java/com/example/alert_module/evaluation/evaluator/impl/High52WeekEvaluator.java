package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.HIGH_52W)
@Component
public class High52WeekEvaluator extends BaseRedisEvaluator {

    public High52WeekEvaluator(RedisTemplate<String, Object> redisTemplate,
                                      AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var daily = getDaily(stockCode);
        var minute = getMinute(stockCode);
        if (daily == null) return false;
        if (minute == null) return false;

        Double price = d(minute.get("price"));
        Double high52w = d(daily.get("highPrice"));

        if (price == null || high52w == null) return false;

        boolean ok = price >= high52w;
        log.info("[HIGH_52W] alertId={} stock={} price={} high52w={} → {}",
                alertId, stockCode,
                String.format("%.2f", price),
                String.format("%.2f", high52w),
                ok ? "충족" : "미충족");
        return ok;
    }
}
