package com.example.alert_module.evaluation.evaluator;

import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ConditionEvaluatorManager {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
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
        String scope = manager.getAlertCondition().getDataScope();
        String redisKey = switch (scope) {
            case "daily" -> "daily:" + stockCode;
            case "minute" -> "minute:" + stockCode;
            default -> "stock:" + stockCode;
        };

        try {
            String json = redisTemplate.opsForValue().get(redisKey);
            if (json == null || json.isBlank()) {
                log.warn("⚠️ [{}] Redis key {} not found or empty", stockCode, redisKey);
                return Collections.emptyMap();
            }

            // ✅ 일단 전체 Map으로 읽음
            Map<String, Object> raw = objectMapper.readValue(json, new TypeReference<>() {});
            Map<String, Double> result = new HashMap<>();

            // ✅ 숫자(Double로 변환 가능한 항목만 필터링)
            raw.forEach((k, v) -> {
                if (v instanceof Number n) {
                    result.put(k, n.doubleValue());
                } else {
                    try {
                        result.put(k, Double.parseDouble(v.toString()));
                    } catch (Exception ignored) {
                        // date, stockCode 같은 문자열은 무시
                    }
                }
            });

            log.info("📊 [{}] metrics loaded from {}: {}", stockCode, redisKey, result.keySet());
            return result;

        } catch (Exception e) {
            log.error("❌ [{}] Redis loadMetrics 실패: {}", redisKey, e.getMessage());
            return Collections.emptyMap();
        }
    }

}
