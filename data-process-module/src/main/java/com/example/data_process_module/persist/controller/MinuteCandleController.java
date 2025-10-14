package com.example.data_process_module.persist.controller;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.data_process_module.persist.dto.MinuteCandleResponse;
import com.example.data_process_module.persist.service.MinuteCandleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/minute-candles")
@RequiredArgsConstructor
public class MinuteCandleController {

    private final MinuteCandleService minuteCandleService;

    @GetMapping("/{stockCode}")
    public ApiResponse<List<MinuteCandleResponse>> getRecent200(@PathVariable String stockCode) {
        List<MinuteCandleResponse> candles = minuteCandleService.getRecent200WithDiff(stockCode);

        if (candles == null || candles.isEmpty()) {
            return ApiResponse.error(ResponseCode.STOCK_NOT_FOUND);
        }

        return ApiResponse.success(ResponseCode.SUCCESS_GET_MINUTE_CANDLE, candles);
    }
}
