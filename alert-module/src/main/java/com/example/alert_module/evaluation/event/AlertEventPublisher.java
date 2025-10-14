package com.example.alert_module.evaluation.event;

import com.example.alert_module.evaluation.event.model.AlertEvent;
import com.example.alert_module.management.entity.AlertConditionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(AlertConditionManager manager) {
        var alert = manager.getAlert();
        AlertEvent event = new AlertEvent(
                alert.getUserId(),
                alert.getStockCode(),
                manager.getAlertCondition().getIndicator(),
                LocalDateTime.now()
        );

        log.info("📤 AlertEvent 발행: {}", event);
        rabbitTemplate.convertAndSend("alert.exchange", "alert.detected", event);
    }
}