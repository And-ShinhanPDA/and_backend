package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.type.ConditionType;
import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.TRAILING_STOP_PERCENT)
@Component
public class TrailingStopPercentEvaluator implements ConditionEvaluator {

    @Override
    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
        Double price = metrics.get("price");       // 현재가
        Double recentHigh = manager.getThreshold2(); // 최근 고가
        Double pctTarget = manager.getThreshold();   // 손절 기준 비율(%)

        if (price == null || recentHigh == null || pctTarget == null) return false;

        // 하락률 = (최근고가 - 현재가) / 최근고가 * 100
        double dropRate = ((recentHigh - price) / recentHigh) * 100.0;

        // 조건: 하락률 ≥ 설정 비율 → 최근 고가 대비 설정 비율 이상 하락 시 손절
        boolean ok = dropRate >= pctTarget;
        log.info("[TRAILING_STOP_PERCENT] alertId={} stock={} price={} recentHigh={} dropRate={} threshold(%)={} → {}",
                manager.getAlert().getUserId(),
                manager.getAlert().getStockCode(),
                String.format("%.2f", price),
                String.format("%.2f", recentHigh),
                String.format("%.2f", dropRate),
                String.format("%.2f", pctTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
