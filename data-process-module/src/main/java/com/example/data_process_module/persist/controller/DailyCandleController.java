package com.example.data_process_module.persist.controller;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.data_process_module.persist.dto.DailyCandleResponse;
import com.example.data_process_module.persist.service.DailyCandleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/daily-candles")
@RequiredArgsConstructor
public class DailyCandleController {

    private final DailyCandleService dailyCandleService;

    @GetMapping("/{stockCode}")
    public ApiResponse<List<DailyCandleResponse>> getRecent200(@PathVariable String stockCode) {
        List<DailyCandleResponse> candles = dailyCandleService.getRecent200WithDiff(stockCode);

        if (candles == null || candles.isEmpty()) {
            return ApiResponse.error(ResponseCode.STOCK_NOT_FOUND);
        }

        return ApiResponse.success(ResponseCode.SUCCESS_GET_DAILY_CANDLE, candles);
    }
}
