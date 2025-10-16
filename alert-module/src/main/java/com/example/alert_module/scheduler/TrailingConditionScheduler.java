package com.example.alert_module.scheduler;

import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrailingConditionScheduler {

    private final AlertConditionManagerRepository alertConditionManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateTrailingConditions() {
        log.info("후행알림 잘돼라");
        List<AlertConditionManager> trailingManagers = alertConditionManager.findAllByAlertCondition_IndicatorIn(List.of(
                "TRAILING_STOP_PRICE", "TRAILING_STOP_PERCENT",
                "TRAILING_BUY_PRICE", "TRAILING_BUY_PERCENT"
        ));

        for (AlertConditionManager m : trailingManagers) {
            String stockCode = m.getAlert().getStockCode();
            Map<String, Object> minute = (Map<String, Object>) redisTemplate.opsForValue().get("minute:" + stockCode);
            if (minute == null) continue;

            Double price = parseDouble(minute.get("price"));
            Double threshold2 = m.getThreshold2();

            switch (m.getAlertCondition().getIndicator()) {
                case "TRAILING_STOP_PRICE" -> {
                    if (price > threshold2) {
                        m.setThreshold2(price);
                    }
                    break;
                }
                case "TRAILING_STOP_PERCENT" -> {
                    if (price > threshold2) {
                        m.setThreshold2(price);
                    }
                }
                case "TRAILING_BUY_PRICE" -> {
                    if (price < threshold2) {
                        m.setThreshold2(price);
                    }
                    break;
                }
                case "TRAILING_BUY_PERCENT" -> {
                    if (price < threshold2) {
                        m.setThreshold2(price);
                    }
                }
            }
        }
    }

    private Double parseDouble(Object value) {
        if (value == null) return null;
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}

