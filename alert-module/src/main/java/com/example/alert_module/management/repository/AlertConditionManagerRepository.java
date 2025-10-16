package com.example.alert_module.management.repository;

import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.entity.AlertConditionManagerId;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AlertConditionManagerRepository
        extends JpaRepository<AlertConditionManager, AlertConditionManagerId> {

    @Query("SELECT acm FROM AlertConditionManager acm " +
            "JOIN FETCH acm.alertCondition " +
            "WHERE acm.alert.id IN :alertIds")
    List<AlertConditionManager> findByAlertIdsWithCondition(@Param("alertIds") List<Long> alertIds);

    void deleteAllByAlertId(Long alertId);

    @Query("SELECT m FROM AlertConditionManager m " +
            "JOIN FETCH m.alertCondition " +
            "WHERE m.alert.id = :alertId")
    List<AlertConditionManager> findByAlertId(@Param("alertId") Long alertId);
    @Modifying
    @Query("DELETE FROM AlertConditionManager acm WHERE acm.alert.id IN :alertIds")
    void deleteByAlertIds(@Param("alertIds") List<Long> alertIds);
    Optional<AlertConditionManager> findById_AlertIdAndId_AlertConditionId(Long alertId, Long conditionId);

    @Query("""
    SELECT m 
    FROM AlertConditionManager m 
    JOIN FETCH m.alert a 
    JOIN FETCH m.alertCondition c 
    WHERE c.indicator IN :indicators
    """)
    List<AlertConditionManager> findAllByAlertCondition_IndicatorIn(List<String> indicators);

}
