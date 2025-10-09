package com.example.alert_module.management.service;

import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertQueryService {

    private final AlertRepository alertRepository;

    /** 오늘 트리거된(실제로 울린) 알림 목록 조회 */
    public List<Alert> getTriggeredToday(Long userId) {
        var today = LocalDate.now();
        return alertRepository.findAllByUserIdAndIsTriggeredTrueAndLastNotifiedAtBetween(
                userId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }
}
