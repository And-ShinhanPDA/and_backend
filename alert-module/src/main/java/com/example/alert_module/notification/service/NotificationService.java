package com.example.alert_module.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendPush(String token, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ FCM 전송 성공: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ FCM 전송 실패: " + e.getMessage());
        }
    }
}
