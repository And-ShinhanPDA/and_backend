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
@ConditionTypeMapping(ConditionType.NEAR_LOW_52W)
@Component
@RequiredArgsConstructor
public class NearLow52WeekEvaluator implements ConditionEvaluator {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> minuteMetrics) {
        Double price = minuteMetrics.get("price");

        String stockCode = manager.getAlert().getStockCode();

        Double low52w = null;
        if (stockCode == null) {
            low52w = minuteMetrics.get("lowPrice");
        } else {
            Map<String, Double> dailyMetrics = loadRedisMetrics("daily:" + stockCode);
            low52w = dailyMetrics.get("lowPrice");
        }

        Double thresholdPct = manager.getThreshold(); // 접근 비율(%)
        if (thresholdPct == null) return false;
        if (price == null || low52w == null || low52w == 0) return false;

        // 고가 대비 현재가 차이율 계산
        double diffRate = Math.abs(((low52w - price) / low52w) * 100.0);

        boolean ok = diffRate <= thresholdPct;
        log.info("[NEAR_LOW_52W] alertId={} stock={} price={} low52w={} diffRate={} threshold(%)={} → {}",
                manager.getAlert().getId(), stockCode,
                String.format("%.2f", price),
                String.format("%.2f", low52w),
                String.format("%.2f", diffRate),
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
