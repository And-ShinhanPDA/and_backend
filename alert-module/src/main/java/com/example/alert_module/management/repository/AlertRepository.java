package com.example.alert_module.history.repository;

import com.example.alert_module.history.entity.AlertHistory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    @Modifying
    @Query("DELETE FROM AlertHistory ah WHERE ah.alert.id IN :alertIds")
    void deleteByAlertIds(@Param("alertIds") List<Long> alertIds);

    List<AlertHistory> findAllByAlert_UserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT h
        FROM AlertHistory h
        JOIN h.alert a
        WHERE a.userId = :userId
          AND a.stockCode = :stockCode
        ORDER BY h.createdAt DESC
    """)
    List<AlertHistory> findAllByUserIdAndStockCode(@Param("userId") Long userId,

                                                   @Param("stockCode") String stockCode);

    List<AlertHistory> findAllByAlert_UserId(@Param("userId") Long userId);

    @Query("""
    SELECT h
    FROM AlertHistory h
    JOIN h.alert a
    WHERE a.userId = :userId
      AND a.stockCode = :stockCode
      AND h.createdAt BETWEEN :start AND :end
    ORDER BY h.createdAt DESC
""")
    List<AlertHistory> findAllByUserIdAndStockCodeAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("stockCode") String stockCode,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT h
    FROM AlertHistory h
    JOIN h.alert a
    WHERE a.userId = :userId
      AND h.createdAt BETWEEN :start AND :end
    ORDER BY h.createdAt DESC
""")
    List<AlertHistory> findAllByUserIdAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    Long countByAlertIdIn(List<Long> alertIds);
}
