package com.example.alert_module.history.service;

import com.example.alert_module.history.dto.AlertHistoryDto;
import com.example.alert_module.history.repository.AlertHistoryRepository;
import com.example.alert_module.management.repository.CompanyRepository;
import com.example.common_service.exception.AlertException;
import com.example.common_service.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertHistoryService {

    private final AlertHistoryRepository alertHistoryRepository;
    private final CompanyRepository companyRepository;


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

    public List<AlertHistoryDto> getAlertHistories(Long userId, String stockCode) {
        if (!companyRepository.existsById(stockCode)) {
            throw new AlertException(ResponseCode.STOCK_NOT_FOUND);
        }


        return alertHistoryRepository.findAllByUserIdAndStockCode(userId, stockCode).stream().map(AlertHistoryDto::from).toList();
    }
}
