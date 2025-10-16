package com.example.alert_module.notification.event;

import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.notification.dto.AlertConditionDto;
import com.example.alert_module.notification.dto.AlertEvent;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.common_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(Alert alert) {
        Map<String, List<AlertConditionDto>> grouped = alert.getConditionManagers().stream()
                .collect(Collectors.groupingBy(
                        m -> m.getAlertCondition().getCategory(),
                        Collectors.mapping(
                                m -> new AlertConditionDto(
                                        m.getAlertCondition().getIndicator(),
                                        m.getThreshold(),
                                        m.getThreshold2()
                                ),
                                Collectors.toList()
                        )
                ));

        var event = new AlertEvent(
                alert.getId(),
                alert.getUserId(),
                alert.getStockCode(),
                alert.getTitle(),
                grouped
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
