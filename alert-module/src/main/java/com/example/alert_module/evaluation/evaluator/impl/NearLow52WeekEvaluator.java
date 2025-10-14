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
@ConditionTypeMapping(ConditionType.NEAR_LOW_52W)
@Component
@RequiredArgsConstructor
public class NearLow52WeekEvaluator implements ConditionEvaluator {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> minuteMetrics) {
        String stockCode = manager.getAlert().getStockCode();

        Map<String, Double> dailyMetrics = loadRedisMetrics("daily:" + stockCode);

        Double thresholdPct = manager.getThreshold(); // 접근 비율(%)
        if (thresholdPct == null) return false;

        // 현재가 가져오기: minute.price → daily.closePrice → daily.openPrice 순
        Double price = minuteMetrics.get("price");

        Double low52w = dailyMetrics.get("lowPrice");
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
