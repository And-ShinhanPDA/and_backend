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
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class IngestService {

    private final TransformService transformService;
    private final DailyCandleRepository dailyCandleRepository;
    private final PersistService persistService;

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

        List<DailyCandleEntity> history =
                dailyCandleRepository.findTop200ByStockCodeOrderByDateAsc(dto.getSymbol());

        List<Double> closePrices = history.stream()
                .map(DailyCandleEntity::getClosePrice)
                .toList();
        List<Integer> volumes = history.stream()
                .map(DailyCandleEntity::getVolume)
                .toList();

        newEntity = transformService.enrichWithIndicators(newEntity, closePrices);
        double avgVol20 = CalculationUtil.calculateAverageVolume(volumes, 20);

        log.info("[1일 데이터 수신] symbol={}, closePrice={}, volume={}",
                newEntity.getStockCode(), newEntity.getClosePrice(), newEntity.getVolume());
        log.info("  → SMA20={}, RSI14={}, Bollinger(상단={}, 하단={}), 평균거래량20={}",
                newEntity.getSma20(), newEntity.getRsi14(),
                newEntity.getBbUpper(), newEntity.getBbLower(),
                avgVol20);
    }

    public void processMinuteData(MinuteDataRequest dto) {
        DailyCandleEntity yesterday =
                dailyCandleRepository.findTop1ByStockCodeOrderByDateDesc(dto.getSymbol());

        double prevClose = yesterday.getClosePrice();
        long prevVolume = yesterday.getVolume();
        double openPrice = yesterday.getOpenPrice();
        double high52w = yesterday.getHighPrice();
        double low52w = yesterday.getLowPrice();

        Map<String, Double> metrics = transformService.calculateIntradayMetrics(
                dto.getPrice(),
                dto.getVolume(),
                openPrice,
                prevClose,
                prevVolume,
                high52w,
                low52w
        );

        double pctVsPrevVol = prevVolume == 0 ? 0.0 : (dto.getVolume() / (double) prevVolume) * 100.0;

        metrics.put("price", dto.getPrice());
        metrics.put("pct_vs_prev_vol", pctVsPrevVol);
        metrics.put("volume", (double) dto.getVolume());

        persistService.saveMinuteData(dto.getSymbol(), metrics);
        log.info("[TRANSFORM] {} 1분 데이터 계산 완료 -> {}", dto.getSymbol(), metrics);

        log.info("[1분 데이터] symbol={}, price={}, volume={}",
                dto.getSymbol(), dto.getPrice(), dto.getVolume());
        log.info("  → 전날 거래량 대비={}% / 시가 대비 변동액={}원 ({}%) / 52주 고가대비={}%, 저가대비={}%",
                metrics.get("volumeRatio"),
                metrics.get("diffFromOpen"),
                metrics.get("diffFromOpenPct"),
                metrics.get("diffFromHigh52wPct"),
                metrics.get("diffFromLow52wPct")
        );
    }
}
