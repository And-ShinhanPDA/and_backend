package com.example.search_module.management.repository;

import com.example.search_module.management.entity.AlertConditionManager;
import com.example.search_module.management.entity.AlertConditionManagerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertConditionManagerRepository extends JpaRepository<AlertConditionManager, AlertConditionManagerId> {}