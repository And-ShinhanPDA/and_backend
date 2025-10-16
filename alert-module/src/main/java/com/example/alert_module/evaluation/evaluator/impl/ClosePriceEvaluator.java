//package com.example.alert_module.evaluation.evaluator.impl;
//
//import com.example.alert_module.evaluation.evaluator.ConditionEvaluator;
//import com.example.alert_module.evaluation.evaluator.type.ConditionType;
//import com.example.alert_module.evaluation.evaluator.type.ConditionTypeMapping;
//import com.example.alert_module.management.entity.AlertConditionManager;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Slf4j
//@ConditionTypeMapping(ConditionType.CLOSE_PRICE)
//@Component
//public class ClosePriceEvaluator implements ConditionEvaluator {
//
//
//    @Override
//    public boolean evaluate(AlertConditionManager manager, Map<String, Double> metrics) {
//        Double close = metrics.get("close");
//        if (close == null) close = metrics.get("prev_close");
//
//        log.info("[CLOSE_PRICE] stock={} 종가={}", manager.getAlert().getStockCode(), close);
//        return false;
//    }
//
//    public Double getClose(String stockCode) {
//        Double close = metrics.get("close");
//        if (close == null) close = metrics.get("prev_close");
//        return close;
//    }
//}