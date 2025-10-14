package com.example.data_process_module.ingest.controller;

import com.example.data_process_module.ingest.dto.DailyDataRequest;
import com.example.data_process_module.ingest.dto.MinuteDataRequest;
import com.example.data_process_module.ingest.service.IngestService;
import com.example.data_process_module.persist.service.MinuteCandleService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
public class IngestController {
    private final IngestService ingestService;
    private final MinuteCandleService minuteCandleService;

    @PostMapping("/minute")
    public ResponseEntity<String> receiveMinute(@RequestBody MinuteDataRequest dto) {
        ingestService.processMinuteData(dto);
        return ResponseEntity.ok("1분 데이터 수신 완료");
    }

    @PostMapping("/daily")
    public ResponseEntity<String> receiveDaily(@RequestBody DailyDataRequest dto) {
        ingestService.processDailyData(dto);
        return ResponseEntity.ok("1일 데이터 수신 완료");
    }

    @PostMapping("/minute/chart")
    public ResponseEntity<Void> ingestMinuteChartData(@RequestBody Map<String, Object> payload) {
        minuteCandleService.saveMinuteChartData(payload);
        return ResponseEntity.ok().build();
    }
}
