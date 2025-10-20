package com.example.alert_module.marketdata.scheduler;

import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.repository.AlertRepository;
import com.example.alert_module.management.repository.CompanyRepository;
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

    private final AlertRepository alertRepository;
    private final PriceCheckService priceFetcher;
    private final PushService pushService;
    private final CompanyRepository companyRepository;

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

                String priceTypeName = priceType.equals("openPrice") ? "시가" : "종가";
                String companyName = companyRepository.findByStockCode(alert.getStockCode())
                        .map(c -> c.getName())
                        .orElse("알 수 없음");

                log.info("🚀 [{}] {}({}) → {}", priceTypeName, companyName, alert.getStockCode(), price);

                pushService.sendPrice(
                        alert.getUserId(),
                        alert.getId(),
                        companyName,
                        price,
                        priceTypeName
                );
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
