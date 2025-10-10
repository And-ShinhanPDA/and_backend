package com.example.alert_module.history.service;

import com.example.alert_module.history.dto.AlertHeatMapDto;
import com.example.alert_module.history.dto.AlertHistoryDto;
import com.example.alert_module.history.dto.AlertHistoryPeriodReq;
import com.example.alert_module.history.repository.AlertHistoryRepository;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.repository.AlertRepository;
import com.example.alert_module.management.repository.CompanyRepository;
import com.example.common_service.exception.AlertException;
import com.example.common_service.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertHistoryService {

    private final AlertRepository alertRepository;
    private final AlertHistoryRepository alertHistoryRepository;
    private final CompanyRepository companyRepository;


    public List<AlertHistoryDto> getTodayHistories(Long userId) {
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

    public List<AlertHistoryDto> getHistoriesByPeriod(Long userId, String stockCode, LocalDate start, LocalDate end) {
        return alertHistoryRepository
                .findAllByUserIdAndStockCodeAndCreatedAtBetween(
                        userId,
                        stockCode,
                        start.atStartOfDay(),
                        end.plusDays(1).atStartOfDay())
                .stream()
                .map(AlertHistoryDto::from)
                .toList();
    }

    public List<AlertHistoryDto> getAlertHistories(Long userId, String stockCode) {
        if (!companyRepository.existsById(stockCode)) {
            throw new AlertException(ResponseCode.STOCK_NOT_FOUND);
        }

        return alertHistoryRepository
                .findAllByUserIdAndStockCode(userId, stockCode)
                .stream().map(AlertHistoryDto::from).toList();
    }

    public AlertHeatMapDto.HeatMapResponseDto getHeatMap(Long userId) {
        List<Alert> alerts = alertRepository.findByUserId(userId);
        System.out.println("alerts" + alerts);

        if (alerts.isEmpty()) {
            return new AlertHeatMapDto.HeatMapResponseDto(List.of(), 0L);
        }

        Map<String, List<Long>> stockAlertMap = alerts.stream()
                .collect(Collectors.groupingBy(
                        Alert::getStockCode,
                        Collectors.mapping(Alert::getId, Collectors.toList())
                ));
        System.out.println("stockAlertMap" + stockAlertMap.toString());
        List<AlertHeatMapDto.HeatMapAlertDto> alertDtos = new ArrayList<>();
        long totalCount = 0L;

        for (Map.Entry<String, List<Long>> entry : stockAlertMap.entrySet()) {
            String stockCode = entry.getKey();
            List<Long> alertIds = entry.getValue();

            long count = alertHistoryRepository.countByAlertIdIn(alertIds);
            totalCount += count;

            alertDtos.add(new AlertHeatMapDto.HeatMapAlertDto(stockCode, count, null));
        }

        return new AlertHeatMapDto.HeatMapResponseDto(alertDtos, totalCount);
    }
}
