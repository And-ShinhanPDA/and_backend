package com.example.alert_module.notification.message;


import com.example.alert_module.management.entity.AlertCondition;
import org.springframework.stereotype.Component;

@Component
public class MessageFormatter {
    public String getTitle(AlertCondition condition) {
        String category = condition.getCategory();

        return switch (category) {
            case "price" -> "💰 가격 알림";
            case "volume_alert" -> "📊 거래량 알림";
            case "rsi_alert" -> "📉 RSI 경고";
            case "bollinger_alert" -> "📈 볼린저 밴드 알림";
            case "fifty_two_week" -> "⏱ 52주 신고/신저가 알림";
            default -> "📢 투자 알림";
        };
    }

    public String getBody(AlertCondition condition, String stockName, Double threshold) {
        String indicator = condition.getIndicator();

        return switch (indicator) {
            case "PRICE_ABOVE" -> String.format("%s의 현재가가 %.2f원 이상입니다.", stockName, threshold);
            case "PRICE_BELOW" -> String.format("%s의 현재가가 %.2f원 이하입니다.", stockName, threshold);
            case "RSI_OVER" -> String.format("%s의 RSI가 %.1f를 초과했습니다 (과매수 구간).", stockName, threshold);
            case "RSI_UNDER" -> String.format("%s의 RSI가 %.1f 미만입니다 (과매도 구간).", stockName, threshold);
            case "VOLUME_AVG_DEV_UP" -> String.format("%s의 거래량이 20일 평균 대비 %.0f%% 이상입니다.", stockName, threshold);
            case "VOLUME_AVG_DEV_DOWN" -> String.format("%s의 거래량이 20일 평균 대비 %.0f%% 이하입니다.", stockName, threshold);
            case "HIGH_52W" -> String.format("%s의 현재가가 52주 최고가를 돌파했습니다!", stockName);
            case "LOW_52W" -> String.format("%s의 현재가가 52주 최저가를 하회했습니다!", stockName);
            default -> String.format("%s: %s 조건이 충족되었습니다.", stockName, condition.getDescription());
        };
    }
}
