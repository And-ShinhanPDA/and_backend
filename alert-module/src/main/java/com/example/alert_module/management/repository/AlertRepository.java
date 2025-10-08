package com.example.alert_module.management.repository;

import com.example.alert_module.management.entity.Alert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserId(Long userId);
    List<Alert> findByUserIdAndStockCode(Long userId, String stockCode);
    List<Alert> findByUserIdAndIsActived(Long userId, Boolean isActived);
    List<Alert> findByUserIdAndStockCodeAndIsActived(Long userId, String stockCode, Boolean isActived);
}
