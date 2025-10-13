package com.example.data_process_module.persist.service;

import com.example.data_process_module.persist.dto.DailyCandleResponse;
import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.repository.DailyCandleRepository;
import java.util.ArrayList;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyCandleService {

    private final DailyCandleRepository dailyCandleRepository;

    public List<DailyCandleResponse> getRecent200WithDiff(String stockCode) {
        List<DailyCandleEntity> entities = dailyCandleRepository.findRecent200ByStockCode(stockCode);

        Collections.reverse(entities);

        List<DailyCandleResponse> responses = new ArrayList<>();

        Double prevClose = null;
        for (DailyCandleEntity e : entities) {
            Double diff = null;
            if (prevClose != null && e.getClosePrice() != null) {
                diff = e.getClosePrice() - prevClose;
            }
            responses.add(DailyCandleResponse.builder()
                    .stockCode(e.getStockCode())
                    .date(e.getDate())
                    .openPrice(e.getOpenPrice())
                    .closePrice(e.getClosePrice())
                    .highPrice(e.getHighPrice())
                    .lowPrice(e.getLowPrice())
                    .volume(e.getVolume())
                    .rsi14(e.getRsi14())
                    .bbUpper(e.getBbUpper())
                    .bbLower(e.getBbLower())
                    .sma5(e.getSma5())
                    .sma10(e.getSma10())
                    .sma20(e.getSma20())
                    .sma30(e.getSma30())
                    .sma50(e.getSma50())
                    .sma100(e.getSma100())
                    .sma200(e.getSma200())
                    .diffFromPrev(diff)
                    .build());
            prevClose = e.getClosePrice();
        }

        return responses;
    }
}

