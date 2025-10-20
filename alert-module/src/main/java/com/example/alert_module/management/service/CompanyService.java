package com.example.alert_module.management.service;

import com.example.alert_module.history.repository.AlertHistoryRepository;
import com.example.alert_module.management.dto.CompanyRes;
import com.example.alert_module.management.dto.GetCompanyRes;
import com.example.alert_module.management.dto.ToggleRequest;
import com.example.alert_module.management.entity.Alert;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import com.example.alert_module.management.repository.AlertPriceRepository;
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
    private final AlertPriceRepository alertPriceRepository;

    public List<CompanyRes> getAllCompanies() {
        return companyRepository.findAllCompanies();
    }

    public List<GetCompanyRes> getAlertedCompanies(Long userId) {
        return alertRepository.findCompanyAlertCountsByUserId(userId);
    }

    @Transactional
    public void deleteAlertCompany(Long userId, String stockCode) {
        if (!companyRepository.existsById(stockCode)) {
            throw new AlertException(ResponseCode.STOCK_NOT_FOUND);
        }

        List<Long> alertIds = alertRepository.findAlertIdsByUserIdAndStockCode(userId, stockCode);

        //stockCode로 alertPrice 지워야됨.
        alertPriceRepository.findByUserIdAndStockCode(userId, stockCode)
                .ifPresent(alertPriceRepository::delete);

        if (alertIds.isEmpty()) {
            throw new AlertException(ResponseCode.NO_EXIST_ALERT);
        }

        alertHistoryRepository.deleteByAlertIds(alertIds);
        alertConditionManagerRepository.deleteByAlertIds(alertIds);
        alertRepository.deleteByAlertIds(alertIds);
    }

    @Transactional
    public void toggleAlertCompany(Long userId, String stockCode, boolean isActived) {
        if (!companyRepository.existsById(stockCode)) {
            throw new AlertException(ResponseCode.STOCK_NOT_FOUND);
        }

        alertRepository.updateIsActivedByUserIdAndStockCode(userId, stockCode, isActived);
    }
}
