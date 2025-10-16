package com.example.alert_module.notification.service;

import com.example.alert_module.notification.dto.AlertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PushService {

    public void send(AlertEvent event) {
        String categorySentence = makeNaturalSentence(event.categories());

        String title = String.format("📈[%s]%s 조건 충족!", event.companyName(), event.title());
        String body = String.format("%s 조건을 만족했습니다.", categorySentence);

        log.info("🔔 [Push] userId={}, title={}, body={}",
                event.userId(), title, body);

        // TODO: 실제 FCM 전송 or WebSocket 메시지 로직 추가
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
            case "sma_alert" -> "이동평균선";
            case "fifty_two_week" -> "52주 고가/저가";
            case "bollinger_alert" -> "볼린저밴드";
            case "volume_alert" -> "거래량";

            default -> key.replace("_", " ");
        };
    }
}
