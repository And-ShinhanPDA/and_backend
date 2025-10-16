package com.example.alert_module.evaluation.evaluator.runner;

import com.example.alert_module.evaluation.evaluator.service.AlertEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationTestRunner implements CommandLineRunner {

    private final AlertEvaluationService evaluationService;

    @Override
    public void run(String... args) throws Exception {
        Long testAlertId = 14L;
        log.info("=== 🧪 알림 평가 테스트 시작 ===");
        evaluationService.evaluateAlert(testAlertId);
        log.info("=== 🧪 테스트 종료 ===");
    }
}
