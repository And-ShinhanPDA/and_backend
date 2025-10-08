package com.example.alert_module.management.repository;

import com.example.alert_module.management.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {}
