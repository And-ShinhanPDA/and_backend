package com.example.alert_module.notification.event;

import com.example.alert_module.notification.event.model.AlertEvent;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.common_service.config.RabbitMQConfig;
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
        var event = new AlertEvent(
                alert.getUserId(),
                alert.getStockCode(),
                manager.getAlertCondition().getIndicator(),
                LocalDateTime.now()
        );

        log.info("""
        📤 [Publish 시도]
        Exchange  = {}
        RoutingKey= {}
        Connection= {}
        Event     = {}
        """,
                RabbitMQConfig.ALERT_EXCHANGE,
                RabbitMQConfig.ALERT_ROUTING_KEY,
                rabbitTemplate.getConnectionFactory(),
                event);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ALERT_EXCHANGE,
                RabbitMQConfig.ALERT_ROUTING_KEY,
                event
        );
    }
}
