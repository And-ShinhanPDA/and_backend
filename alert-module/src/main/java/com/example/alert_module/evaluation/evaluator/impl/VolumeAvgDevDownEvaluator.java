package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.VOLUME_AVG_DEV_DOWN)
@Component
@RequiredArgsConstructor
public class VolumeAvgDevDownEvaluator implements ConditionEvaluator {

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

        boolean ok = deviationRate <= thresholdPct;
        log.info("[VOLUME_AVG_DEV_DOWN] alertId={} stock={} volume={} avgVol20={} deviationRate={} threshold(%)={} → {}",
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
        Map<String, Double> result = new HashMap<>();

        try {
            var type = redisTemplate.type(redisKey);
            if (type == null) {
                log.warn("⚠️ Redis key {} not found", redisKey);
                return result;
            }

            // ✅ key 타입에 따라 분기
            Map<Object, Object> raw;
            if (type.name().equalsIgnoreCase("hash")) {
                // Hash 구조일 때 (HGETALL)
                raw = redisTemplate.opsForHash().entries(redisKey);
            } else if (type.name().equalsIgnoreCase("string")) {
                // String(JSON) 구조일 때 (GET)
                String json = redisTemplate.opsForValue().get(redisKey);
                if (json == null || json.isBlank()) return result;
                raw = new ObjectMapper().readValue(json, new TypeReference<>() {});
            } else {
                log.warn("⚠️ Redis key {} has unsupported type: {}", redisKey, type);
                return result;
            }

            // ✅ 공통 변환 로직
            raw.forEach((k, v) -> {
                try {
                    result.put(k.toString(), Double.parseDouble(v.toString()));
                } catch (NumberFormatException ignored) {}
            });

            log.info("📊 Redis metrics loaded [{}]: {} keys", redisKey, result.size());
            return result;

        } catch (Exception e) {
            log.error("❌ Redis loadRedisMetrics 실패 [{}]: {}", redisKey, e.getMessage());
            return result;
        }
    }

}
