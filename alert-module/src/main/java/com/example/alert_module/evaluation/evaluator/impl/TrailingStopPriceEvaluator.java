package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.TRAILING_STOP_PRICE)
@Component
public class TrailingStopPriceEvaluator extends BaseRedisEvaluator {

    public TrailingStopPriceEvaluator(RedisTemplate<String, Object> redisTemplate,
                                     AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var minute = getMinute(stockCode);
        if (minute == null) return false;

        Double price = d(minute.get("price"));       // 현재가
        Double recentHigh = manager.getThreshold2(); // 최근 고가
        Double dropTarget = manager.getThreshold();  // 손절 기준 금액

        if (price == null || recentHigh == null || dropTarget == null) return false;

        // 조건: 최근 고가 - 현재가 ≥ 설정 금액 → 최근 고점 대비 손절폭 초과
        boolean ok = (recentHigh - price) >= dropTarget;
        log.info("[TRAILING_STOP_PRICE] alertId={} stock={} price={} recentHigh={} dropTarget={} → {}",
                alertId, stockCode,
                String.format("%.2f", price),
                String.format("%.2f", recentHigh),
                String.format("%.2f", dropTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
