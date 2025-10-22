package com.example.alert_module.notification.service;

import com.example.alert_module.notification.dto.PushMessage;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        List<String> targets = new ArrayList<>(tokens);
        int maxAttempts = 3;
        int attempt = 1;

        while (attempt <= maxAttempts && !targets.isEmpty()) {
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .addAllTokens(targets)
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
                log.info("📊 [FCM 전송 결과 - 시도 {}회차] 전체={}, 성공={}, 실패={}", attempt, targets.size(), success, failure);

                List<String> retryTokens = new ArrayList<>();

                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    SendResponse sendResponse = responses.get(i);
                    String token = targets.get(i);

                    if (!sendResponse.isSuccessful()) {
                        FirebaseMessagingException ex = (FirebaseMessagingException) sendResponse.getException();
                        MessagingErrorCode code = ex.getMessagingErrorCode();

                        // 🔁 서버 오류 or 네트워크 불안정 → 재시도 대상
                        if (code == MessagingErrorCode.INTERNAL || code == MessagingErrorCode.UNAVAILABLE) {
                            log.warn("🔁 [FCM 재시도 대상] token={}, error={}", token, code);
                            retryTokens.add(token);
                        }

                        // ⚠️ 인증/설정 오류 → 서버 점검 필요
                        else if (code == MessagingErrorCode.THIRD_PARTY_AUTH_ERROR || code == MessagingErrorCode.SENDER_ID_MISMATCH) {
                            log.error("⚠️ [FCM 인증/서버 설정 문제] token={}, error={}", token, code);
                        }
                    }
                }

                // ✅ 모두 성공했으면 종료
                if (retryTokens.isEmpty()) {
                    log.info("✅ [FCM 전송 완료] 시도 {}회차에 전체 성공", attempt);
                    return;
                }

                // 🔁 실패한 토큰만 다시 시도
                targets = retryTokens;
                attempt++;

            } catch (FirebaseMessagingException e) {
                log.error("❌ [FCM 전송 중 예외] attempt={}, errorCode={}, message={}", attempt, e.getMessagingErrorCode(), e.getMessage());
                return;
            }
        }

        if (!targets.isEmpty()) {
            log.error("❌ [FCM 전송 최종 실패] 실패 토큰 개수={}", targets.size());
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
