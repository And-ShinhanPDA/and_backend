package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.BOLLINGER_LOWER_TOUCH)
@Component
public class BollingerLowerEvaluator extends BaseRedisEvaluator {
    public BollingerLowerEvaluator(RedisTemplate<String, Object> redisTemplate, AlertConditionManagerRepository managerRepo) {
        super(redisTemplate, managerRepo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var daily = getDaily(stockCode);
        if (daily == null) return false;

        Double bbLower = d(daily.get("bbLower")); // 일별 볼린저밴드
        Double threshold = manager.getThreshold(); // 설정값
        if (bbLower == null || threshold == null) return false;

        boolean ok = bbLower >= threshold;
        log.info("[BOLLINGER_LOWER_TOUCH] alertId={} stock={} bbLower={} threshold={} → {}",
                alertId, stockCode,
                String.format("%.2f", bbLower), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");

        return ok;
    }

}
