package com.example.alert_module.marketdata.scheduler;

import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.entity.AlertPrice;
import com.example.alert_module.management.repository.AlertPriceRepository;
import com.example.alert_module.management.repository.AlertRepository;
import com.example.alert_module.management.repository.CompanyRepository;
import com.example.alert_module.marketdata.service.PriceCheckService;
import com.example.alert_module.notification.service.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceScheduler {

    private final AlertRepository alertRepository;
    private final AlertPriceRepository alertPriceRepository; // ✅ 추가
    private final CompanyRepository companyRepository;
    private final PriceCheckService priceFetcher;
    private final PushService pushService;

    // 🕘 매일 오전 9시 (시가 알림)
    @Scheduled(cron = "0 0 9 * * MON-FRI", zone = "Asia/Seoul")
    public void sendOpenPriceAlerts() {
        processPriceAlerts("openPrice");
    }

    // 🕞 매일 오후 3시 30분 (종가 알림)
    @Scheduled(cron = "0 30 15 * * MON-FRI", zone = "Asia/Seoul")
    public void sendClosePriceAlerts() {
        processPriceAlerts("closePrice");
    }

    private void processPriceAlerts(String priceType) {
        // 1️⃣ 시가/종가 ON 상태인 알림만 조회
        List<AlertPrice> activePriceAlerts = alertPriceRepository.findByTogglePriceTrue();
        log.info("🔔 [{}] 시가·종가 알림 대상: {}건", priceType, activePriceAlerts.size());

        for (AlertPrice alertPrice : activePriceAlerts) {
            Long userId = alertPrice.getUserId();
            String stockCode = alertPrice.getStockCode();

            priceFetcher.fetchPrice(stockCode).ifPresent(data -> {
                Double price = parseDouble(data.get(priceType));
                if (price == null) return;

                String priceTypeName = priceType.equals("openPrice") ? "시가" : "종가";
                String companyName = companyRepository.findByStockCode(stockCode)
                        .map(c -> c.getName())
                        .orElse("알 수 없음");

                log.info("🚀 [{}] {}({}) → {}", priceTypeName, companyName, stockCode, price);

                pushService.sendPrice(
                        userId,
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
