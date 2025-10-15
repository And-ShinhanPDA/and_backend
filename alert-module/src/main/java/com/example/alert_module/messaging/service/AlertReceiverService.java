package com.example.alert_module.messaging.service;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class AlertReceiverService {

    @RabbitListener(queues = "alert.queue")
    public void receive(String message) {
        System.out.println("Received message from RabbitMQ: " + message);
    }
}