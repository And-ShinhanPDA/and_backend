package com.example.alert_module.evaluation.repository;

import com.example.alert_module.evaluation.entity.ConditionSearch;
import com.example.alert_module.evaluation.entity.ConditionSearchId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionSearchRepository extends JpaRepository<ConditionSearch, ConditionSearchId> {
    List<ConditionSearch> findByAlert_Id(Long alertId);
}

