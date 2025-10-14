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
@ConditionTypeMapping(ConditionType.NEAR_HIGH_52W)
@Component
@RequiredArgsConstructor
public class NearHigh52WeekEvaluator implements ConditionEvaluator {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> minuteMetrics) {
        String stockCode = manager.getAlert().getStockCode();

        Map<String, Double> dailyMetrics = loadRedisMetrics("daily:" + stockCode);

        Double thresholdPct = manager.getThreshold(); // 접근 비율(%)
        if (thresholdPct == null) return false;

        // 현재가 가져오기: minute.price → daily.closePrice → daily.openPrice 순
        Double price = minuteMetrics.get("price");

        // 52주 최고가
        Double high52w = dailyMetrics.get("highPrice");
        if (price == null || high52w == null || high52w == 0) return false;

        // 고가 대비 현재가 차이율 계산
        double diffRate = Math.abs(((high52w - price) / high52w) * 100.0);

        boolean ok = diffRate <= thresholdPct;
        log.info("[NEAR_HIGH_52W] alertId={} stock={} price={} high52w={} diffRate={} threshold(%)={} → {}",
                manager.getAlert().getId(), stockCode,
                String.format("%.2f", price),
                String.format("%.2f", high52w),
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
