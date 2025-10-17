package com.example.alert_module.notification.message;


import com.example.alert_module.notification.dto.AlertEvent;
import com.example.alert_module.notification.service.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = "alert.queue", autoStartup = "true")
public class AlertNotifier {
    private final PushService pushService;

    @RabbitHandler
    public void handleAlert(AlertEvent event) {
        log.info("📥 MQ 이벤트 수신: {}", event);
        pushService.send(event);
    }
}