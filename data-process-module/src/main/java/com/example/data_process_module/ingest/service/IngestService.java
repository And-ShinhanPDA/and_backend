package com.example.data_process_module.ingest.service;


import com.example.data_process_module.ingest.dto.DailyDataRequest;
import com.example.data_process_module.ingest.dto.MinuteDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IngestService {
    public void processMinuteData(MinuteDataRequest dto) {
        log.info("[1분 데이터] symbol={}, price={}, volume={}",
                dto.getSymbol(), dto.getPrice(), dto.getVolume());
    }

    public void processDailyData(DailyDataRequest dto) {
        log.info("[1일 데이터] symbol={}, prevClose={}, prevVolume={}, openPrice={}, 52wHigh={}, 52wLow={}",
                dto.getSymbol(), dto.getPrevClose(), dto.getPrevVolume(),
                dto.getOpenPrice(), dto.getHigh52w(), dto.getLow52w());
    }
}
