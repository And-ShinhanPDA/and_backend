package com.example.alert_module;

import com.example.alert_module.evaluation.service.AlertDetectService;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.entity.AlertCondition;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertRepository;
import com.example.alert_module.management.repository.AlertConditionRepository;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@SpringBootApplication
public class AlertIntegrationRunner implements CommandLineRunner {

    @Autowired private AlertRepository alertRepository;
    @Autowired private AlertConditionRepository conditionRepository;
    @Autowired private AlertConditionManagerRepository managerRepository;
    @Autowired private AlertDetectService alertDetectService;
    @Autowired private RedisTemplate<String, Object> redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(AlertIntegrationRunner.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) {
        String stockCode = "005930";

        // 🔹 Redis 테스트 데이터 세팅
        Map<String, Object> minuteMetrics = Map.of(
                "price", 72000.0,
                "volumeRatio", 130.0 // 전일 대비 거래량 130%
        );
        Map<String, Object> dailyMetrics = Map.of(
                "highPrice", 70000.0,
                "lowPrice", 68000.0
        );

        redisTemplate.delete("minute:" + stockCode);
        redisTemplate.delete("daily:" + stockCode);
        redisTemplate.opsForHash().putAll("minute:" + stockCode, minuteMetrics);
        redisTemplate.opsForHash().putAll("daily:" + stockCode, dailyMetrics);

        // 🔹 Alert 생성
        Alert alert = new Alert();
        alert.setUserId(1L);
        alert.setStockCode(stockCode);
        alert.setTitle("거래량 급증 알림");
        alert.setIsActived(true);
        alertRepository.save(alert);

        // 🔹 Condition 생성
        AlertCondition cond = new AlertCondition();
        cond.setCategory("volume");
        cond.setIndicator("VOLUME_CHANGE_PERCENT_DOWN"); // evaluator 매핑됨
        cond.setDataScope("minute"); // ✅ Redis minute:* 데이터 참조
        cond.setDescription("전일 대비 거래량이 설정된 비율 이상 증가할 때");
        conditionRepository.save(cond);

        // 🔹 Manager 생성
        AlertConditionManager manager = AlertConditionManager.of(alert, cond, 120.0, null); // 기준: 120%
        managerRepository.save(manager);

        // 🔹 알림 감지 실행
        alertDetectService.detectForStock(stockCode);

        System.out.println("✅ 테스트 실행 완료!");
    }
}
