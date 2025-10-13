package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.TRAILING_BUY_PERCENT)
@Component
public class TrailingBuyPercentEvaluator extends BaseRedisEvaluator {
    public TrailingBuyPercentEvaluator(RedisTemplate<String, Object> redisTemplate,
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
        Double pctTarget = manager.getThreshold();   // 매수 기준 비율(%)

        if (price == null || recentHigh == null || pctTarget == null) return false;

        // 상승률 = (현재가 - 최근고가) / 최근고가 * 100
        double riseRate = ((price - recentHigh) / recentHigh) * 100.0;

        // 조건: 상승률 ≥ 설정 비율 → 최근 고가 대비 설정 비율 이상 상승 시 매수
        boolean ok = riseRate >= pctTarget;
        log.info("[TRAILING_BUY_PERCENT] alertId={} stock={} price={} recentHigh={} riseRate={} threshold(%)={} → {}",
                alertId, stockCode,
                String.format("%.2f", price),
                String.format("%.2f", recentHigh),
                String.format("%.2f", riseRate),
                String.format("%.2f", pctTarget),
                ok ? "충족" : "미충족");
        return ok;
    }
}
