package com.example.alert_module.notification.event;

import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.repository.CompanyRepository;
import com.example.alert_module.notification.dto.AlertConditionDto;
import com.example.alert_module.notification.dto.AlertEvent;
import com.example.common_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final CompanyRepository companyRepository;


    public void publish(Alert alert, String alertType, String stockCode) {

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

        String companyName = companyRepository.findByStockCode(stockCode)
                .map(c -> c.getName())
                .orElse("알 수 없음");

        Set<String> categories = grouped.keySet();

        var event = new AlertEvent(
                alert.getId(),
                alert.getUserId(),
                stockCode,
                companyName,
                alert.getTitle(),
                categories,
                alert.getIsTriggered()
        );

        String routingKey = switch (alertType.toUpperCase()) {
            case "CONDITION" -> RabbitMQConfig.ALERT_CONDITION_ROUTING_KEY;
            case "COMPANY" -> RabbitMQConfig.ALERT_COMPANY_ROUTING_KEY;
            default -> RabbitMQConfig.ALERT_COMPANY_ROUTING_KEY;
        };

        log.info("""
        📤 [RabbitMQ Publish]
        Exchange  = {}
        RoutingKey= {}
        Event     = {}
        """,
                RabbitMQConfig.ALERT_EXCHANGE,
                routingKey,
                event);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ALERT_EXCHANGE,
                routingKey,
                event
        );
    }
}
