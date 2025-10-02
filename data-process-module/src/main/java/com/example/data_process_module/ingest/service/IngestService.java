package com.example.data_process_module.ingest.service;


import com.example.data_process_module.ingest.dto.DailyDataRequest;
import com.example.data_process_module.ingest.dto.MinuteDataRequest;
import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.service.PersistService;
import com.example.data_process_module.transform.service.TransformService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class IngestService {

    private final TransformService transformService;
    private final PersistService persistService;

    public void processMinuteData(MinuteDataRequest dto) {
        log.info("[1분 데이터] symbol={}, price={}, volume={}",
                dto.getSymbol(), dto.getPrice(), dto.getVolume());
    }

    public void processDailyData(DailyDataRequest dto) {

        DailyCandleEntity entity = DailyCandleEntity.builder()
                .stockCode(dto.getSymbol())
                .date(LocalDateTime.now())
                .openPrice(dto.getOpenPrice())
                .closePrice(dto.getPrevClose())
                .highPrice(dto.getHigh52w())
                .lowPrice(dto.getLow52w())
                .volume((int) dto.getPrevVolume())
                .build();

        List<Double> closePrices = Arrays.asList(70000.0, 71000.0, 72000.0, 73000.0, dto.getPrevClose());

        entity = transformService.enrichWithIndicators(entity, closePrices);

        persistService.saveDaily(entity);
    }
}
