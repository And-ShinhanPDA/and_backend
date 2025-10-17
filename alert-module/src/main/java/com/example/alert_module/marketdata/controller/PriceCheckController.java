package com.example.alert_module.marketdata.controller;

import com.example.alert_module.marketdata.scheduler.PriceCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/price-check")
@RequiredArgsConstructor
public class PriceCheckController {

    private final PriceCheckService priceCheckService;

    @GetMapping("/price/{code}")
    public void checkPrice(@PathVariable String code) {
        priceCheckService.testTodayPrice(code);
    }
}
