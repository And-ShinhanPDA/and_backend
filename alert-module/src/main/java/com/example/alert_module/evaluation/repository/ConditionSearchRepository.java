package com.example.alert_module.evaluation.repository;

import com.example.alert_module.evaluation.entity.ConditionSearch;
import com.example.alert_module.evaluation.entity.ConditionSearchId;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ConditionSearchRepository extends JpaRepository<ConditionSearch, ConditionSearchId> {
    List<ConditionSearch> findByAlert_Id(Long alertId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ConditionSearch c " +
            "SET c.isTriggered = :isTriggered, c.triggerDate = :triggerDate " +
            "WHERE c.alert.id = :alertId AND c.stockCode = :stockCode")
    int updateTriggerState(@Param("alertId") Long alertId,
                           @Param("stockCode") String stockCode,
                           @Param("isTriggered") Boolean isTriggered,
                           @Param("triggerDate") LocalDateTime triggerDate);
}

