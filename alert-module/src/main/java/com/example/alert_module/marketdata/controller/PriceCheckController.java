package com.example.alert_module.marketdata.controller;


import com.example.alert_module.marketdata.scheduler.PriceScheduler;
import com.example.alert_module.marketdata.service.PriceCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price-check")
@RequiredArgsConstructor
public class PriceCheckController {

    private final PriceScheduler priceScheduler;
    @PostMapping("/open")  public void open()  { priceScheduler.sendOpenPriceAlerts(); }
    @PostMapping("/close") public void close() { priceScheduler.sendClosePriceAlerts(); }
}
