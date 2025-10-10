package com.example.alert_module.history.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlertHistoryPeriodReq(
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate start,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate end
) {}