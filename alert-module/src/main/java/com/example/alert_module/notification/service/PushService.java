package com.example.alert_module.notification.service;

import com.example.alert_module.notification.event.model.AlertEvent;
import com.example.user_module.fcm.entity.FcmToken;
import com.example.user_module.fcm.repository.FcmRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    private final FcmRepository fcmRepository;

    /**
     * 📨 AlertEvent 기반으로 FCM 알림 전송
     */
    public void send(AlertEvent event) {
//        FcmToken fcmToken = fcmRepository.findByUserId(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 FCM 토큰이 존재하지 않습니다."));
//
//        String token = fcmToken.getFcmToken();
//        String token = "dmlL26W0X05Mq8S_e8o9gP:APA91bE-VAkhYtT0sK2DkbBBW22zm7OIj_AAO741hHcOcMu4KeKDm0Vc6bUk1WlkVpyhIXYG73xBMZJwXmlavMnXpcDcYy7OOa2FcPfwgnckrBKG1hjF2ow";

        String title = "📈 조건 감지 알림";
        String body = String.format("%s 종목이 %s 조건을 만족했습니다.",
                event.stockCode(), event.conditionType());

        log.info("🔔 [Push] userId={}, stockCode={}, condition={}",
                event.userId(), event.stockCode(), event.conditionType());

        List<FcmToken> tokens = fcmRepository.findByUserIdAndActivedTrue((event.userId()));
        for (FcmToken token : tokens) {
            send(token.getFcmToken(), title, body);
        }

//        send(token, title, body);
    }

    /**
     * 📨 FCM 전송 (토큰, 제목, 내용 직접 지정)
     */
    public void send(String token, String title, String body) {
        if (token == null || token.isBlank()) {
            log.warn("⚠️ 유효하지 않은 FCM 토큰: {}", token);
            return;
        }

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
            log.info("✅ FCM 전송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("❌ FCM 전송 실패: {}", e.getMessage(), e);
        }
    }
}
