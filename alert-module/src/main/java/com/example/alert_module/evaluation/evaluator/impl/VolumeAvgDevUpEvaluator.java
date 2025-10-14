package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.VOLUME_AVG_DEV_UP)
@Component
@RequiredArgsConstructor
public class VolumeAvgDevUpEvaluator implements ConditionEvaluator {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> minuteMetrics) {
        String stockCode = manager.getAlert().getStockCode();

        Map<String, Double> dailyMetrics = loadRedisMetrics("daily:" + stockCode);

        Double thresholdPct = manager.getThreshold(); // 기준 백분율 (%)
        if (thresholdPct == null) return false;

        // 현재 거래량 (minute 우선, 없으면 daily)
        Double volume = minuteMetrics.get("volume");
        if (volume == null) return false;

        // 평균 거래량 (20일 기준)
        Double avgVol20 = dailyMetrics.get("avgVol20");
        if (avgVol20 == null ) return false;

        // 거래량 변화율 계산
        double deviationRate = ((volume - avgVol20) / avgVol20) * 100.0;

        boolean ok = deviationRate >= thresholdPct;
        log.info("[VOLUME_AVG_DEV_UP] alertId={} stock={} volume={} avgVol20={} deviationRate={} threshold(%)={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.0f", volume),
                String.format("%.0f", avgVol20),
                String.format("%.2f", deviationRate),
                String.format("%.2f", thresholdPct),
                ok ? "충족" : "미충족");
        return ok;
    }

    private Map<String, Double> loadRedisMetrics(String redisKey) {
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(redisKey);
        Map<String, Double> result = new HashMap<>();
        raw.forEach((k, v) -> {
            try {
                result.put(k.toString(), Double.parseDouble(v.toString()));
            } catch (NumberFormatException ignored) {}
        });
        return result;
    }
}
