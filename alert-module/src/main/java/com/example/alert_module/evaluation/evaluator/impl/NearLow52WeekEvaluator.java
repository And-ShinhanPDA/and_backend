package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionTypeMapping(ConditionType.NEAR_LOW_52W)
@Component
public class NearLow52WeekEvaluator extends BaseRedisEvaluator {

    public NearLow52WeekEvaluator(RedisTemplate<String, Object> redisTemplate,
                                   AlertConditionManagerRepository repo) {
        super(redisTemplate, repo);
    }

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        var manager = getManager(alertId, conditionId);
        var daily = getDaily(stockCode);
        var minute = getMinute(stockCode);

        if (daily == null) return false;
        if (minute == null) return false;
        Double thresholdPct = manager.getThreshold(); // 접근 비율(%)
        if (thresholdPct == null) return false;

        // 현재가 가져오기: minute.price → daily.closePrice → daily.openPrice 순
        Double price = d(minute.get("price"));

        // 52주 최고가
        Double low52w = d(daily.get("lowPrice"));
        if (price == null || low52w == null || low52w == 0) return false;

        // 고가 대비 현재가 차이율 계산
        double diffRate = Math.abs(((low52w - price) / low52w) * 100.0);

        boolean ok = diffRate <= thresholdPct;
        log.info("[NEAR_LOW_52W] alertId={} stock={} price={} low52w={} diffRate={} threshold(%)={} → {}",
                alertId, stockCode,
                String.format("%.2f", price),
                String.format("%.2f", low52w),
                String.format("%.2f", diffRate),
                String.format("%.2f", thresholdPct),
                ok ? "충족" : "미충족");
        return ok;
    }
}
