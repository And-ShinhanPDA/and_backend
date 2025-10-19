package com.example.alert_module.notification.service;

import com.example.alert_module.notification.dto.PushMessage;
import com.example.alert_module.notification.factory.PushMessageFactory;
import com.example.user_module.fcm.entity.FcmToken;
import com.example.user_module.fcm.repository.FcmRepository;

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
    private final PushMessageFactory messageFactory;
    private final NotificationService notificationService;

    /**
     * 📨 AlertEvent 기반으로 FCM 알림 전송
     **/
    public void send(AlertEvent event) {
        String categorySentence = makeNaturalSentence(event.categories());

//        String token = fcmToken.getFcmToken();
//        String token = "dmlL26W0X05Mq8S_e8o9gP:APA91bE-VAkhYtT0sK2DkbBBW22zm7OIj_AAO741hHcOcMu4KeKDm0Vc6bUk1WlkVpyhIXYG73xBMZJwXmlavMnXpcDcYy7OOa2FcPfwgnckrBKG1hjF2ow";


        PushMessage message = messageFactory.createAlertCompany(
                event.companyName(),
                event.title(),
                event.isTriggered(),
                categorySentence
        );

        log.info("🔔 [Push] userId={}, title={}, body={}", event.userId(), message.title(), message.body());

        saveAlertHistory(event, message.body());

        List<FcmToken> tokens = fcmRepository.findByUserIdAndActivedTrue((event.userId()));
        for (FcmToken token : tokens) {
            notificationService.send(token.getFcmToken(), message);
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
