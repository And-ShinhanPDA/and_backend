package com.example.alert_module.evaluation.repository;


import com.example.alert_module.evaluation.entity.ConditionSearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionSearchResultRepository extends JpaRepository<ConditionSearchResult, Long> {

    List<ConditionSearchResult> findByAlert_Id(Long alertId);

    List<ConditionSearchResult> findByAlert_IdAndStockCode(Long alertId, String stockCode);

    List<ConditionSearchResult> findByStockCode(String stockCode);

    List<ConditionSearchResult> findByAlert_IdAndIsTriggeredTrue(Long alertId);

}
