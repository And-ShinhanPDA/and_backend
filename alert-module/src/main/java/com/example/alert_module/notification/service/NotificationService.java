package com.example.alert_module.notification.service;

import com.example.alert_module.notification.dto.PushMessage;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    public void sendAll(List<String> tokens, PushMessage message) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn("⚠️ FCM 토큰 없음, 전송 중단");
            return;
        }

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(message.title())
                        .setBody(message.body())
                        .build())
                .putData("title", message.title())
                .putData("body", message.body())
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);

            int success = response.getSuccessCount();
            int failure = response.getFailureCount();
            log.info("📊 [FCM 전송 결과] 전체={}, 성공={}, 실패={}", tokens.size(), success, failure);

            List<String> invalidTokens = response.getResponses().stream()
                    .filter(r -> !r.isSuccessful())
                    .map(r -> r.getException().getErrorCode() + " : " + r.getException().getMessage())
                    .collect(Collectors.toList());
            if (!invalidTokens.isEmpty()) {
                log.warn("⚠️ [FCM 실패 토큰] {}", invalidTokens);
            }

        } catch (FirebaseMessagingException e) {
            log.error("❌ [FCM 전송 오류] {}", e.getMessage(), e);
        }
    }

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
            log.info("✅ [단일 FCM 전송 성공] {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("❌ [단일 FCM 전송 실패] code={}, msg={}",
                    e.getErrorCode(), e.getMessage());
        }
    }
}
