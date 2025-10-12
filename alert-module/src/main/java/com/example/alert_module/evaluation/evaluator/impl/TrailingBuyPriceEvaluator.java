package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.TRAILING_BUY_PRICE)
@Component
public class TrailingBuyPriceEvaluator extends BaseRedisEvaluator {

    public TrailingBuyPriceEvaluator(RedisTemplate<String, Object> redisTemplate,
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
        Double riseTarget = manager.getThreshold();  // 상승 기준 금액

        if (price == null || recentHigh == null || riseTarget == null) return false;

        // ✅ 조건: 현재가 - 최근 고가 ≥ 설정 금액 → 고점 돌파 시 매수 트리거
        boolean ok = (price - recentHigh) >= riseTarget;
        log.info("[TRAILING_BUY_PRICE] alertId={} stock={} price={} recentHigh={} riseTarget={} → {}",
                alertId, stockCode,
                String.format("%.2f", price),
                String.format("%.2f", recentHigh),
                String.format("%.2f", riseTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
