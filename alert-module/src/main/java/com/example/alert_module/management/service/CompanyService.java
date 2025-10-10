package com.example.alert_module.management.service;

import com.example.alert_module.history.repository.AlertHistoryRepository;
import com.example.alert_module.management.dto.CompanyRes;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import com.example.alert_module.management.repository.AlertRepository;
import com.example.alert_module.management.repository.CompanyRepository;
import com.example.common_service.exception.AlertException;
import com.example.common_service.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final AlertRepository alertRepository;
    private final AlertConditionManagerRepository alertConditionManagerRepository;
    private final AlertHistoryRepository alertHistoryRepository;
    private final CompanyRepository companyRepository;

    public List<CompanyRes> getAllCompanies() {
        return companyRepository.findAllCompanies();
    }

    public List<CompanyRes> getAlertedCompanies(Long userId) {
        return alertRepository.findDistinctCompaniesByUserId(userId);
    }

    @Transactional
    public void deleteAlertCompany(Long userId, String stockCode) {
        List<Long> alertIds = alertRepository.findAlertIdsByUserIdAndStockCode(userId, stockCode);

        if (alertIds.isEmpty()) {
            throw new AlertException(ResponseCode.NO_EXIST_ALERT);
        }
        alertHistoryRepository.deleteByAlertIds(alertIds);
        alertConditionManagerRepository.deleteByAlertIds(alertIds);
        alertRepository.deleteByAlertIds(alertIds);
    }
}
