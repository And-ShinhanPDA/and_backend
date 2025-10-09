package com.example.alert_module.history.service;

import com.example.alert_module.history.dto.AlertHistoryDto;
import com.example.alert_module.history.repository.AlertHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertHistoryService {

    private final AlertHistoryRepository alertHistoryRepository;


    public List<AlertHistoryDto> getTodayHistoryByUser(Long userId) {
        var today = LocalDate.now();

        return alertHistoryRepository
                .findAllByAlert_UserIdAndCreatedAtBetween(
                        userId,
                        today.atStartOfDay(),
                        today.plusDays(1).atStartOfDay())
                .stream()
                .map(AlertHistoryDto::from)
                .toList();
    }
}
