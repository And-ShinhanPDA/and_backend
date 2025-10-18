package com.example.alert_module.notification.service;

import com.example.user_module.fcm.repository.FcmRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import com.example.alert_module.history.entity.AlertHistory;
import com.example.alert_module.history.repository.AlertHistoryRepository;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.notification.dto.AlertEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    private final FcmRepository fcmRepository;
    private final AlertHistoryRepository alertHistoryRepository;

    /**
     * 📨 AlertEvent 기반으로 FCM 알림 전송
     **/
    public void send(AlertEvent event) {
        String categorySentence = makeNaturalSentence(event.categories());

//        FcmToken fcmToken = fcmRepository.findByUserId(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 FCM 토큰이 존재하지 않습니다."));
//
//        String token = fcmToken.getFcmToken();
        String token = "dmlL26W0X05Mq8S_e8o9gP:APA91bE-VAkhYtT0sK2DkbBBW22zm7OIj_AAO741hHcOcMu4KeKDm0Vc6bUk1WlkVpyhIXYG73xBMZJwXmlavMnXpcDcYy7OOa2FcPfwgnckrBKG1hjF2ow";

//        List<FcmToken> tokens = fcmRepository.findByUserIdAndActivedTrue((event.userId()));
//        for (FcmToken token : tokens) {
//            send(token.getFcmToken(), title, body);
//        }

        if (event.isTriggered()) {
            String title = String.format("📈[%s]%s 조건 충족!", event.companyName(), event.title());
            String body = String.format("%s 조건을 만족했습니다.", categorySentence);

            log.info("🔔 [Push] userId={}, title={}, body={}",
                    event.userId(), title, body);

            saveAlertHistory(event, body);
            sendFcm(token, title, body);
        } else {
            String title = String.format("📈[%s]%s 조건 미충족!", event.companyName(), event.title());
            String body = String.format("조건을 벗어났습니다.", categorySentence);

            log.info("🔔 [Push] userId={}, title={}, body={}",
                    event.userId(), title, body);

            saveAlertHistory(event, body);
            sendFcm(token, title, body);
        }

    }

    /**
     * 📨 FCM 전송 (토큰, 제목, 내용 직접 지정)
     */
    public void sendFcm(String token, String title, String body) {
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


    private void saveAlertHistory(AlertEvent event, String body) {
        try {
            Alert alert = Alert.builder()
                    .id(event.alertId())
                    .build();

            AlertHistory history = AlertHistory.builder()
                    .alert(alert)
                    .indicatorSnapshot(body)
                    .build();

            alertHistoryRepository.save(history);

            log.info("🧾 [AlertHistory 저장 완료] alertId={}, snapshot={}", event.alertId(), body);
        } catch (Exception e) {
            log.error("❌ AlertHistory 저장 실패: {}", e.getMessage());
        }
    }

    private String makeNaturalSentence(Set<String> categories) {
        if (categories == null || categories.isEmpty()) return "조건";

        List<String> readable = categories.stream()
                .map(this::prettyCategory)
                .toList();

        if (readable.size() == 1) return readable.get(0);
        if (readable.size() == 2) return String.join(" 그리고 ", readable);
        return String.join(", ", readable.subList(0, readable.size() - 1))
                + " 그리고 " + readable.get(readable.size() - 1);
    }

    private String prettyCategory(String key) {
        return switch (key) {
            case "price" -> "가격";
            case "rsi_alert" -> "RSI";
            case "sma_alert" -> "SMA";
            case "fifty_two_week" -> "52주";
            case "bollinger_alert" -> "볼린저밴드";
            case "volume_alert" -> "거래량";

            default -> key.replace("_", " ");
        };
    }

}
