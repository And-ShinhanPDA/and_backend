package com.example.alert_module.evaluation.evaluator;

import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ConditionEvaluatorManager {

    private final StringRedisTemplate redisTemplate;
    private final Map<String, ConditionEvaluator> evaluators = new HashMap<>();

    public ConditionEvaluatorManager(List<ConditionEvaluator> evaluatorList, StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        evaluatorList.forEach(evaluator -> {
            ConditionTypeMapping mapping = evaluator.getClass().getAnnotation(ConditionTypeMapping.class);
            if (mapping != null) {
                evaluators.put(mapping.value().name(), evaluator);
            }
        });
        log.info("✅ 등록된 Evaluator 수: {}", evaluators.size());
    }

    /**
     * (1) AlertCondition의 indicator로 evaluator 식별
     * (2) AlertCondition.dataScope으로 minute/daily Redis 선택
     */
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        String indicator = manager.getAlertCondition().getIndicator();
        ConditionEvaluator evaluator = evaluators.get(indicator);
        if (evaluator == null) {
            log.warn("⚠️ Evaluator 없음: {}", indicator);
            return false;
        }
        return evaluator.evaluate(manager, metrics);
    }

    /**
     * Redis에서 metric 데이터 로드 (minute/daily 선택)
     */
    public Map<String, Double> loadMetrics(AlertConditionManager manager) {
        String stockCode = manager.getAlert().getStockCode();
        String scope = manager.getAlertCondition().getDataScope();  // "minute" or "daily"
        String redisKey = switch (scope) {
            case "daily" -> "daily:" + stockCode;
            case "minute" -> "minute:" + stockCode;
            default -> "stock:" + stockCode; // fallback
        };

        Map<Object, Object> raw = redisTemplate.opsForHash().entries(redisKey);
        Map<String, Double> result = new HashMap<>();
        raw.forEach((k, v) -> {
            try {
                result.put(k.toString(), Double.parseDouble(v.toString()));
            } catch (NumberFormatException ignored) {}
        });

        log.debug("📊 [{}] metrics loaded from {}: {}", stockCode, redisKey, result.keySet());
        return result;
    }
}
