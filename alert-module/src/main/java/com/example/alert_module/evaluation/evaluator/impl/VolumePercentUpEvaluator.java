package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.VOLUME_CHANGE_PERCENT_UP)
@Component
public class VolumePercentUpEvaluator extends BaseRedisEvaluator {
    public VolumePercentUpEvaluator(RedisTemplate<String, Object> redisTemplate, AlertConditionManagerRepository managerRepo) {
        super(redisTemplate, managerRepo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var minute = getMinute(stockCode);
        if (minute == null) return false;

        Double volume = d(minute.get("volumeRatio")); // 전날 거래량 대비 백분율
        Double threshold = manager.getThreshold(); // 설정값
        if (volume == null || threshold == null) return false;

        boolean ok = volume >= threshold;
        log.info("[VOLUME_CHANGE_PERCENT_UP] alertId={} stock={} volume={} threshold={} → {}",
                alertId, stockCode,
                String.format("%.2f", volume), String.format("%.2f", threshold),
                ok ? "충족" : "미충족");

        return ok;
    }
}
