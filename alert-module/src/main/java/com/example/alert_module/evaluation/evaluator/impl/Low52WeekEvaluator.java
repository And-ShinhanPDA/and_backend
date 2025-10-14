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
@ConditionTypeMapping(ConditionType.LOW_52W)
@Component
@RequiredArgsConstructor
public class Low52WeekEvaluator implements ConditionEvaluator {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> minuteMetrics) {
        String stockCode = manager.getAlert().getStockCode();

        // 🔹 52주 데이터는 daily Redis에서 가져옴
        Map<String, Double> dailyMetrics = loadRedisMetrics("daily:" + stockCode);

        Double price = minuteMetrics.get("price");
        Double low52w = dailyMetrics.get("lowPrice"); // Redis에서 저장된 필드명에 맞게 수정

        if (price == null || low52w == null) return false;

        boolean ok = price >= low52w;

        log.info("[LOW_52W] userId={} stock={} price={} low52w={} → {}",
                manager.getAlert().getUserId(), stockCode,
                String.format("%.2f", price),
                String.format("%.2f", low52w),
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
