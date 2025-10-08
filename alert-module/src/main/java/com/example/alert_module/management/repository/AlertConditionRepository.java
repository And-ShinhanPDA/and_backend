package com.example.alert_module.management.repository;

import com.example.alert_module.management.entity.AlertCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AlertConditionRepository extends JpaRepository<AlertCondition, Long> {
    List<AlertCondition> findByIndicatorIn(Collection<String> indicators);
}
