package com.example.data_process_module.persist.controller;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.data_process_module.persist.service.StockPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockPriceController {

    private final StockPriceService stockPriceService;

    @GetMapping("/{stockCode}")
    public ApiResponse<Double> getCurrentPrice(@PathVariable String stockCode) {
        Double price = stockPriceService.getCurrentPrice(stockCode);

        if (price == null) {
            return ApiResponse.error(ResponseCode.STOCK_NOT_FOUND);
        }

        return ApiResponse.success(ResponseCode.SUCCESS_GET_CURRENT_PRICE, price);
    }
}
