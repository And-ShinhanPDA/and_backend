package com.example.alert_module.history.repository;

import com.example.alert_module.history.entity.DailyCandle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DailyCandleRepository extends JpaRepository<DailyCandle, DailyCandle.PK> {
    DailyCandle findTopByStockCodeAndDateBeforeOrderByDateDesc(String stockCode, LocalDateTime date);

}
