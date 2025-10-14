package com.example.alert_module.management.repository;

import com.example.alert_module.management.dto.CompanyRes;
import com.example.alert_module.management.entity.Alert;

import java.time.LocalDateTime;
import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserId(Long userId);
    List<Alert> findByUserIdAndStockCode(Long userId, String stockCode);
    List<Alert> findByUserIdAndIsActived(Long userId, Boolean isActived);
    List<Alert> findByUserIdAndStockCodeAndIsActived(Long userId, String stockCode, boolean isActived);

    @Query("SELECT DISTINCT new com.example.alert_module.management.dto.CompanyRes(a.stockCode, c.name) " +
            "FROM Alert a INNER JOIN Company c ON a.stockCode = c.stockCode " +
            "WHERE a.userId = :userId")
    List<CompanyRes> findDistinctCompaniesByUserId(Long userId);

    @Query("SELECT a.id FROM Alert a WHERE a.userId = :userId AND a.stockCode = :stockCode")
    List<Long> findAlertIdsByUserIdAndStockCode(@Param("userId") Long userId, @Param("stockCode") String stockCode);

    @Modifying
    @Query("DELETE FROM Alert a WHERE a.id IN :alertIds")
    void deleteByAlertIds(@Param("alertIds") List<Long> alertIds);

    @Modifying
    @Query("UPDATE Alert a SET a.isActived = :isActived " +
            "WHERE a.userId = :userId AND a.stockCode = :stockCode")
    void updateIsActivedByUserIdAndStockCode(@Param("userId") Long userId,
                                             @Param("stockCode") String stockCode,
                                             @Param("isActived") boolean isActived);

    List<Alert> findByUserIdAndIsTriggered(Long userId, Boolean isTriggered);

    List<Alert> findByIsActivedAndStockCode(boolean isActived, String stockCode);
}
