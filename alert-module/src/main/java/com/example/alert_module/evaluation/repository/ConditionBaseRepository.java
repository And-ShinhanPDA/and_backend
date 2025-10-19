package com.example.alert_module.evaluation.repository;

import com.example.alert_module.evaluation.entity.ConditionBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionBaseRepository extends JpaRepository<ConditionBase, Long> {

    // 특정 alert_id에 해당하는 기준 데이터 조회
    List<ConditionBase> findByAlertId(Long alertId);

    // 특정 종목 기준 데이터 조회
    List<ConditionBase> findByStockCode(String stockCode);

    // alert_id + stock_code 조합으로 단일 기준 데이터 조회
    ConditionBase findByAlertIdAndStockCode(Long alertId, String stockCode);
}
