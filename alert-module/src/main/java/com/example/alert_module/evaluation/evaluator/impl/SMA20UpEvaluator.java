package com.example.alert_module.evaluation.evaluator.impl;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.evaluation.evaluator.ConditionType;
import com.example.alert_module.evaluation.evaluator.ConditionTypeMapping;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionTypeMapping(ConditionType.SMA_20_UP)
@Component
@RequiredArgsConstructor
public class SMA20UpEvaluator implements ConditionEvaluator {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AlertConditionManagerRepository conditionManagerRepository;

    @Override
    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
        // 1️⃣ AlertConditionManager 조회 (threshold 가져오기)
        AlertConditionManager manager = conditionManagerRepository
                .findById_AlertIdAndId_AlertConditionId(alertId, conditionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "조건 매핑 정보 없음: alertId=" + alertId + ", conditionId=" + conditionId));

        Double threshold = manager.getThreshold(); // 사용자가 설정한 기준값

        // 2️⃣ Redis에서 daily 데이터 가져오기
        String redisKey = "daily:" + stockCode;
        Map<String, Object> dailyData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);

        if (dailyData == null) {
            System.err.println("Redis에서 데이터를 찾을 수 없음: " + redisKey);
            return false;
        }

        // 3️⃣ SMA20 값 읽기
        Double sma20 = toDouble(dailyData.get("sma20"));
        if (sma20 == null || threshold == null) return false;

        // 4️⃣ 조건 판단: SMA20 <= threshold
        boolean isSatisfied = sma20 <= threshold;

        log.info(
                "[SMA_20_UP] alertId={} stock={} sma20={} threshold={} → {}",
                alertId, stockCode, String.format("%.2f", sma20),
                String.format("%.2f", threshold),
                isSatisfied ? "충족" : "불충족"
        );

        return isSatisfied;
    }

    private Double toDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(obj.toString()); }
        catch (Exception e) { return null; }
    }
}
