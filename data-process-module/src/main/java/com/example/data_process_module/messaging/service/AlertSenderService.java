package com.example.data_process_module.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.example.common_service.config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
public class AlertSenderService {

    private final RabbitTemplate rabbitTemplate;

    public void sendTestMessage(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ALERT_EXCHANGE,
                RabbitMQConfig.ALERT_TEST_ROUTING_KEY,
                message
        );
        System.out.println("📤 Sent message to RabbitMQ: " + message);
    }
}