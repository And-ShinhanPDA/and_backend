package com.example.alert_module.evaluation.evaluator;

import com.example.alert_module.management.entity.AlertConditionManager;

import java.util.Map;

public interface ConditionEvaluator {

    boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics);
}
