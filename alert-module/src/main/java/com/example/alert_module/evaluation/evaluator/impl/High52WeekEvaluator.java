package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.HIGH_52W)
@Component
@RequiredArgsConstructor
public class High52WeekEvaluator implements ConditionEvaluator {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> minuteMetrics) {
        String stockCode = manager.getAlert().getStockCode();

        // 🔹 52주 데이터는 daily Redis에서 가져옴
        Map<String, Double> dailyMetrics = loadRedisMetrics("daily:" + stockCode);

        Double price = minuteMetrics.get("price");
        Double high52w = dailyMetrics.get("highPrice"); // Redis에서 저장된 필드명에 맞게 수정

        if (price == null || high52w == null) return false;

        boolean ok = price >= high52w;

        log.info("[HIGH_52W] userId={} stock={} price={} high52w={} → {}",
                manager.getAlert().getUserId(), stockCode,
                String.format("%.2f", price),
                String.format("%.2f", high52w),
                ok ? "충족" : "미충족");

        return ok;
    }

    /** ✅ Redis 해시에서 double 값 파싱 후 반환 */
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