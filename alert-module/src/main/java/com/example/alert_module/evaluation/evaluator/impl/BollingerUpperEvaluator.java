package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.BOLLINGER_UPPER_TOUCH)
@Component
public class BollingerUpperEvaluator extends BaseRedisEvaluator {

    public BollingerUpperEvaluator(RedisTemplate<String, Object> redisTemplate, AlertConditionManagerRepository managerRepo) {
        super(redisTemplate, managerRepo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var daily = getDaily(stockCode);
        if (daily == null) return false;

        Double bbUpper = d(daily.get("bbUpper")); // 일별 볼린저밴드
        Double threshold = manager.getThreshold(); // 설정값
        if (bbUpper == null || threshold == null) return false;

        boolean ok = bbUpper <= threshold;
        log.info("[BOLLINGER_UPPER_TOUCH] alertId={} stock={} bbUpper={} threshold={} → {}",
                alertId, stockCode,
                String.format("%.2f", bbUpper), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");

        return ok;
    }
}
