package com.example.alert_module.marketdata.repository;

import com.example.alert_module.management.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceCheckRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByIsPriceTrue();
}
