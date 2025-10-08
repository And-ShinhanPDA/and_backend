package com.example.search_module.management.repository;

import com.example.search_module.management.entity.AlertCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertConditionRepository extends JpaRepository<AlertCondition, Long> {
    Optional<AlertCondition> findByIndicatorAndOperator(String indicator, String operator);
}