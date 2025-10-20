package com.example.alert_module.management.repository;

import com.example.alert_module.management.entity.AlertPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertPriceRepository extends JpaRepository<AlertPrice, Long> {
    Optional<AlertPrice> findByUserIdAndStockCode(Long userId, String stockCode);
}
