package com.example.alert_module.evaluation.evaluator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class test {
    private final ConditionEvaluatorFactory evaluatorFactory;

    @GetMapping
    public String testCondition() {

        // ▶️ 나중에 indicator가 바뀌면 여기 한 줄만 수정하면 됨
        ConditionType type = ConditionType.VOLUME_AVG_DEV_DOWN;

        boolean result = evaluatorFactory
                .getEvaluator(type)
                .evaluate(20L, 108L, "005930");

        log.info("🔵 [{}] 결과: {}", type, result);
        return result ? "조건 충족" : "조건 불충족";
    }
}
