package com.example.alert_module.management.repository;

import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.entity.AlertConditionManagerId;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlertConditionManagerRepository
        extends JpaRepository<AlertConditionManager, AlertConditionManagerId> {

    @Query("SELECT acm FROM AlertConditionManager acm " +
            "JOIN FETCH acm.alertCondition " +
            "WHERE acm.alert.id IN :alertIds")
    List<AlertConditionManager> findByAlertIdsWithCondition(@Param("alertIds") List<Long> alertIds);
}
