package com.example.alert_module.evaluation.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ConditionEvaluatorFactory {

    private final Map<ConditionType, ConditionEvaluator> evaluatorMap = new EnumMap<>(ConditionType.class);

    public ConditionEvaluatorFactory(List<ConditionEvaluator> evaluators) {
        for (ConditionEvaluator evaluator : evaluators) {
            ConditionTypeMapping mapping = evaluator.getClass().getAnnotation(ConditionTypeMapping.class);
            if (mapping != null) {
                evaluatorMap.put(mapping.value(), evaluator);
                log.info("✅ Registered evaluator: {} → {}", mapping.value(), evaluator.getClass().getSimpleName());
            }
        }
    }

    public ConditionEvaluator getEvaluator(ConditionType type) {
        ConditionEvaluator evaluator = evaluatorMap.get(type);
        if (evaluator == null) {
            throw new IllegalArgumentException("등록되지 않은 ConditionEvaluator: " + type);
        }
        return evaluator;
    }
}
