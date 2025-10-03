package com.example.data_process_module.ingest.service;


import com.example.data_process_module.ingest.dto.DailyDataRequest;
import com.example.data_process_module.ingest.dto.MinuteDataRequest;
import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.repository.DailyCandleRepository;
import com.example.data_process_module.persist.service.PersistService;
import com.example.data_process_module.transform.service.TransformService;
import com.example.data_process_module.transform.util.CalculationUtil;
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
    private final DailyCandleRepository dailyCandleRepository;

    public void processMinuteData(MinuteDataRequest dto) {
        log.info("[1분 데이터] symbol={}, price={}, volume={}",
                dto.getSymbol(), dto.getPrice(), dto.getVolume());
    }

    public void processDailyData(DailyDataRequest dto) {

        DailyCandleEntity newEntity = DailyCandleEntity.builder()
                .stockCode(dto.getSymbol())
                .date(LocalDateTime.now())
                .openPrice(dto.getOpenPrice())
                .closePrice(dto.getPrevClose())
                .highPrice(dto.getHigh52w())
                .lowPrice(dto.getLow52w())
                .volume((int) dto.getPrevVolume())
                .build();

        List<DailyCandleEntity> history = dailyCandleRepository.findTop200ByStockCodeOrderByDateAsc(dto.getSymbol());
        List<Double> closePrices = history.stream()
                .map(DailyCandleEntity::getClosePrice)
                .toList();
        List<Integer> volumes = history.stream()
                .map(DailyCandleEntity::getVolume)
                .toList();

        newEntity = transformService.enrichWithIndicators(newEntity, closePrices);
        double avgVol20 = CalculationUtil.calculateAverageVolume(volumes, 20);

        log.info("[1일 데이터 수신] symbol={}, closePrice={}, volume={}", newEntity.getStockCode(), newEntity.getClosePrice(), newEntity.getVolume());
        log.info("  → SMA20={}, RSI14={}, Bollinger(상단={}, 하단={}), 평균거래량20={}",
                newEntity.getSma20(), newEntity.getRsi14(),
                newEntity.getBbUpper(), newEntity.getBbLower(),
                avgVol20);


    }
}
