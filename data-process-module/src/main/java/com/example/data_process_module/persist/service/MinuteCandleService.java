package com.example.data_process_module.persist.service;

import com.example.data_process_module.persist.entity.MinuteCandleEntity;
import com.example.data_process_module.persist.repository.MinuteCandleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinuteCandleService {

    private final MinuteCandleRepository minuteCandleRepository;

    public void saveMinuteChartData(Map<String, Object> payload) {
        try {
            String stockCode = (String) payload.get("stock_code"); // 종목코드 (없으면 별도 key 사용)
            String dateStr = (String) payload.get("stck_bsop_date");   // 예: 20251014
            String timeStr = (String) payload.get("stck_cntg_hour");   // 예: 153000

            LocalDateTime dateTime = LocalDateTime.parse(
                    dateStr + timeStr,
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            );

            MinuteCandleEntity entity = MinuteCandleEntity.builder()
                    .stockCode(stockCode)
                    .date(dateTime)
                    .openPrice(toDouble(payload.get("stck_oprc")))
                    .closePrice(toDouble(payload.get("stck_prpr")))
                    .highPrice(toDouble(payload.get("stck_hgpr")))
                    .lowPrice(toDouble(payload.get("stck_lwpr")))
                    .volume(toInt(payload.get("cntg_vol")))
                    .build();

            minuteCandleRepository.save(entity);
            log.info("💾 [Saved] {} - {}", stockCode, dateTime);

        } catch (Exception e) {
            log.error("❌ Failed to save minute chart data: {}", e.getMessage(), e);
        }
    }

    private Double toDouble(Object o) {
        try {
            return o == null ? null : Double.parseDouble(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer toInt(Object o) {
        try {
            return o == null ? null : Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
