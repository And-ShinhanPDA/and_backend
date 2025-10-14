package com.example.data_process_module.persist.service;

import com.example.data_process_module.persist.dto.DailyCandleResponse;
import com.example.data_process_module.persist.dto.MinuteCandleResponse;
import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.entity.MinuteCandleEntity;
import com.example.data_process_module.persist.repository.MinuteCandleRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public List<MinuteCandleResponse> getRecent200WithDiff(String stockCode) {
        List<MinuteCandleEntity> entities = minuteCandleRepository.findRecent200ByStockCode(stockCode);

        Collections.reverse(entities);

        List<MinuteCandleResponse> responses = new ArrayList<>();

        Double prevClose = null;
        for (MinuteCandleEntity e : entities) {
            Double diff = null;
            if (prevClose != null && e.getClosePrice() != null) {
                diff = e.getClosePrice() - prevClose;
            }
            if (diff != null) {
                responses.add(MinuteCandleResponse.builder()
                        .stockCode(e.getStockCode())
                        .date(e.getDate())
                        .openPrice(e.getOpenPrice())
                        .closePrice(e.getClosePrice())
                        .highPrice(e.getHighPrice())
                        .lowPrice(e.getLowPrice())
                        .volume(e.getVolume())
                        .diffFromPrev(diff)
                        .build());
            }
            prevClose = e.getClosePrice();
        }

        if (responses.size() > 200) {
            responses = responses.subList(1, 201);
        }

        return responses;
    }

    public void saveMinuteChartData(Map<String, Object> payload) {
        try {
            String stockCode = (String) payload.get("stock_code");
            String dateStr = (String) payload.get("stck_bsop_date");
            String timeStr = (String) payload.get("stck_cntg_hour");

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
