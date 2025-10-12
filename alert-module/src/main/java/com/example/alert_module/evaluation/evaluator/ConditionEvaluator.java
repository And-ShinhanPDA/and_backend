package com.example.alert_module.evaluation.evaluator;

public interface ConditionEvaluator {
    /**
     * 특정 유저의 조건이 현재 충족되는지 평가합니다.
     *
     * @param alertId 알림 ID (alert 테이블 PK)
     * @param conditionId 조건 ID (DB의 alertCondition.pk)
     * @param stockCode 종목 코드 (예: "005930")
     * @return true - 조건 충족 / false - 불충족
     */
    boolean evaluate(Long alertId, Long conditionId, String stockCode);
}
