package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.VOLUME_AVG_DEV_DOWN)
@Component
public class VolumeAvgDevDownEvaluator extends BaseRedisEvaluator{

    public VolumeAvgDevDownEvaluator(RedisTemplate<String, Object> redisTemplate,
                                   AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var daily = getDaily(stockCode);
        var minute = getMinute(stockCode);

        if (daily == null || minute == null) return false;
        Double thresholdPct = manager.getThreshold(); // 기준 백분율 (%)
        if (thresholdPct == null) return false;

        // 현재 거래량 (minute 우선, 없으면 daily)
        Double volume = d(minute.get("volume"));
        if (volume == null) return false;

        // 평균 거래량 (20일 기준)
        Double avgVol20 = d(daily.get("avgVol20"));
        if (avgVol20 == null ) return false;

        // 거래량 변화율 계산
        double deviationRate = ((volume - avgVol20) / avgVol20) * 100.0;

        boolean ok = deviationRate <= thresholdPct;
        log.info("[VOLUME_AVG_DEV_DOWN] alertId={} stock={} volume={} avgVol20={} deviationRate={} threshold(%)={} → {}",
                alertId, stockCode,
                String.format("%.0f", volume),
                String.format("%.0f", avgVol20),
                String.format("%.2f", deviationRate),
                String.format("%.2f", thresholdPct),
                ok ? "충족" : "미충족");
        return ok;
    }
}
