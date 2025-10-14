package com.example.alert_module.evaluation.evaluator;

import com.example.alert_module.management.entity.AlertConditionManager;

import java.util.Map;

public interface ConditionEvaluator {
    /**
     * 특정 유저의 조건이 현재 충족되는지 평가합니다.
     *
     * @param alertId 알림 ID (alert 테이블 PK)
     * @param conditionId 조건 ID (DB의 alertCondition.pk)
     * @param stockCode 종목 코드 (예: "005930")
     * @return true - 조건 충족 / false - 불충족
     */
    boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics);
}
