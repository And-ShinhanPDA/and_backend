package com.example.alert_module.notification.service;

import com.example.alert_module.notification.dto.PushMessage;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void send(String token, PushMessage message) {
        if (token == null || token.isBlank()) {
            log.warn("⚠️ 유효하지 않은 FCM 토큰: {}", token);
            return;
        }

        Message fcmMessage = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(message.title())
                        .setBody(message.body())
                        .build())
                .putData("title", message.title())
                .putData("body", message.body())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            log.info("✅ FCM 전송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("❌ FCM 전송 실패: {}", e.getMessage(), e);
        }
    }
}