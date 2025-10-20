//package com.example.alert_module.evaluation.evaluator.impl;
//
//import com.example.alert_module.evaluation.evaluator.type.ConditionType;
//import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
//import com.example.alert_module.evaluation.evaluator.base.BaseRedisEvaluator;
//import com.example.alert_module.management.repository.AlertConditionManagerRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@ConditionTypeMapping(ConditionType.OPEN_PRICE)
//@Component
//public class OpenPriceEvaluator extends BaseRedisEvaluator {
//
//    public OpenPriceEvaluator(RedisTemplate<String, Object> redisTemplate,
//                              AlertConditionManagerRepository repo) {
//        super(redisTemplate, repo);
//    }
//
//    @Override
//    public boolean evaluate(Long alertId, Long conditionId, String stockCode) {
//        var daily = getDaily(stockCode);
//        if (daily == null) return false;
//
//        Double open = d(daily.get("open"));
//        if (open == null) return false;
//
//        log.info("[OPEN_PRICE] stock={} 시가={}", stockCode, open);
//        return false;
//    }
//
//    public Double getOpen(String stockCode) {
//        var daily = getDaily(stockCode);
//        return daily != null ? d(daily.get("open")) : null;
//    }
//}
//
