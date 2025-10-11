package com.example.alert_module.evaluation.evaluator.base;

import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

@RequiredArgsConstructor
public abstract class BaseRedisEvaluator implements ConditionEvaluator {

    protected final RedisTemplate<String, Object> redisTemplate;
    protected final AlertConditionManagerRepository managerRepo;

    protected AlertConditionManager getManager(Long alertId, Long conditionId) {
        return managerRepo.findById_AlertIdAndId_AlertConditionId(alertId, conditionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "조건 매핑 정보 없음: alertId=" + alertId + ", conditionId=" + conditionId));
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getDaily(String stockCode) {
        return (Map<String, Object>) redisTemplate.opsForValue().get("daily:" + stockCode);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getMinute(String stockCode) {
        return (Map<String, Object>) redisTemplate.opsForValue().get("minute:" + stockCode);
    }

    protected Double d(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }
}
