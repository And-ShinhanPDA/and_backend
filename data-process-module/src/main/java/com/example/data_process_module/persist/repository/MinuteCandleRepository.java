package com.example.data_process_module.persist.repository;

import com.example.data_process_module.persist.entity.MinuteCandleEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MinuteCandleRepository extends JpaRepository<MinuteCandleEntity, MinuteCandleEntity.PK> {
    @Query(value = """
        SELECT * FROM minuteCandle 
        WHERE stock_code = :stockCode
        ORDER BY date DESC
        LIMIT 201
        """, nativeQuery = true)
    List<MinuteCandleEntity> findRecent200ByStockCode(@Param("stockCode") String stockCode);

    @Query(value = "SELECT close_price FROM minuteCandle WHERE stock_code = :stockCode ORDER BY date DESC LIMIT 1", nativeQuery = true)
    Optional<Double> findLatestClosePriceByStockCode(@Param("stockCode") String stockCode);
}

