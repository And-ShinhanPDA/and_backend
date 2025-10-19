package com.example.alert_module.marketdata.scheduler;

import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.repository.AlertRepository;
import com.example.alert_module.marketdata.service.PriceCheckService;
import com.example.alert_module.notification.dto.AlertEvent;
import com.example.alert_module.notification.service.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceScheduler {

    private final AlertRepository alertRepository;  // ✅ 변경
    private final PriceCheckService priceFetcher;
    private final PushService pushService;

    @Scheduled(cron = "0 0 9 * * MON-FRI", zone = "Asia/Seoul")
    public void sendOpenPriceAlerts() {
        processPriceAlerts("openPrice");
    }

    @Scheduled(cron = "0 30 15 * * MON-FRI", zone = "Asia/Seoul")
    public void sendClosePriceAlerts() {
        processPriceAlerts("closePrice");
    }

    private void processPriceAlerts(String priceType) {
        List<Alert> alerts = alertRepository.findActivePriceAlerts();
        log.info("🔔 [{}] 알림 대상: {}건", priceType, alerts.size());

        for (Alert alert : alerts) {
            priceFetcher.fetchPrice(alert.getStockCode()).ifPresent(data -> {
                Double price = parseDouble(data.get(priceType));
                if (price == null) return;

                String title = priceType.equals("openPrice") ? "시가 알림" : "종가 알림";
                String body = String.format("%s의 %s는 %.2f원입니다.", alert.getTitle(),
                        priceType.equals("openPrice") ? "시가" : "종가", price);

                AlertEvent event = AlertEvent.builder()
                        .userId(alert.getUserId())
                        .alertId(alert.getId())
                        .companyName(alert.getTitle())
                        .title(title)
                        .isTriggered(true)
                        .categories(Set.of("price"))
                        .build();

                log.info("🚀 [{}] {}({}) → {}", title, alert.getTitle(), alert.getStockCode(), price);
                pushService.send(event);
            });
        }
    }

    private Double parseDouble(Object obj) {
        try {
            return obj == null ? null : Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }


}
