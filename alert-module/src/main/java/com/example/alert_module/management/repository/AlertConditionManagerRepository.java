package com.example.alert_module.management.repository;

import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.entity.AlertConditionManagerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertConditionManagerRepository
        extends JpaRepository<AlertConditionManager, AlertConditionManagerId> {}
