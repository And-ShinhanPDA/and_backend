package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.PRICE_RATE_BASE_DOWN)
@Component
public class PriceRateBaseDownEvaluator extends BaseRedisEvaluator {

    public PriceRateBaseDownEvaluator(RedisTemplate<String, Object> redisTemplate,
                                    AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var minute = getMinute(stockCode);
        if (minute == null) return false;

        Double price = d(minute.get("price"));       // 현재가
        Double basePrice = manager.getThreshold2();  // 기준 시점 가격
        Double pctTarget = manager.getThreshold();   // 설정 백분율
        if (price == null || basePrice == null || pctTarget == null) return false;

        // 상승률 = (현재가 - 기준가) / 기준가 * 100
        double rate = ((price - basePrice) / basePrice) * 100.0;

        // 상승 조건: 상승률 ≥ 설정 백분율
        boolean ok = rate <= pctTarget;
        log.info("[PRICE_RATE_BASE_DOWN] alertId={} stock={} price={} basePrice={} rate={} threshold={} → {}",
                alertId, stockCode,
                String.format("%.2f", price),
                String.format("%.2f", basePrice),
                String.format("%.2f", rate),
                String.format("%.2f", pctTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
