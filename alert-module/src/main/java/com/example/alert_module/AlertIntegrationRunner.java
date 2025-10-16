//package com.example.alert_module;
//
//import com.example.alert_module.evaluation.service.AlertDetectService;
//import com.example.alert_module.management.entity.Alert;
//import com.example.alert_module.management.entity.AlertCondition;
//import com.example.alert_module.management.entity.AlertConditionManager;
//import com.example.alert_module.management.repository.AlertRepository;
//import com.example.alert_module.management.repository.AlertConditionRepository;
//import com.example.alert_module.management.repository.AlertConditionManagerRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.annotation.EnableRabbit;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Map;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//
//@Component
//@RequiredArgsConstructor
//public class AlertIntegrationRunner {
//    private final AlertDetectService alertDetectService;
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void runAfterStartup() {
//        String stockCode = "373220";
//
//        Map<String, Object> dailyMetrics = Map.of(
//                "highPrice", 70000.0,
//                "lowPrice", 68000.0,
//                "rsi14", 25.0
//        );
//
//        redisTemplate.opsForHash().putAll("daily:" + stockCode, dailyMetrics);
//        alertDetectService.detectForStock(stockCode);
//
//        System.out.println("✅ 테스트 실행 완료!");
//
//    }
//}
