package com.example.alert_module.management.controller;

import com.example.alert_module.management.dto.CompanyRes;
import com.example.alert_module.management.service.CompanyService;
import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<?> getCompanies(
            @AuthUser Long userId,
            @RequestParam(required = false, defaultValue = "false") boolean alerted
    ) {
        if (alerted) {
            List<CompanyRes> alertedCompanies = companyService.getAlertedCompanies(userId);
            return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS_GET_ALERTED_COMPANIES, alertedCompanies));
        }

        List<CompanyRes> allCompanies = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS_GET_ALL_COMPANIES, allCompanies));
    }

    @DeleteMapping("/{stockCode}")
    public ResponseEntity<?> deleteAlertCompany(@AuthUser Long userId,
                                                @PathVariable String stockCode) {
        companyService.deleteAlertCompany(userId, stockCode);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS_DELETE_ALERT_COMPANY, stockCode));
    }

}
