package com.example.alert_module.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConditionTriggeredRes {
    private String conditionName;      // 조건 이름
    private Long activeCompanyCount;   // 활성화된 기업 수
}
