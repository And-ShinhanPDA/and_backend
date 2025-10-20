package com.example.alert_module.notification.message;


import com.example.alert_module.notification.dto.AlertEvent;
import com.example.alert_module.notification.service.PushService;
import com.example.common_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertNotifier {

    private final PushService pushService;

    @RabbitListener(queues = RabbitMQConfig.ALERT_COMPANY_QUEUE)
    public void handleCompany(AlertEvent event) {
        log.info("📥 기업별 알림 수신: {}", event);
        pushService.send(event);
    }

    @RabbitListener(queues = RabbitMQConfig.ALERT_CONDITION_QUEUE)
    public void handleCondition(AlertEvent event) {
        log.info("📥 조건검색 알림 수신: {}", event);
        pushService.sendCondition(event);
    }
}