package com.example.data_process_module.persist.repository;


import com.example.data_process_module.persist.entity.DailyCandleEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DailyCandleRepository extends JpaRepository<DailyCandleEntity, DailyCandleEntity.PK> {
    List<DailyCandleEntity> findTop200ByStockCodeOrderByDateAsc(String stockCode);

    DailyCandleEntity findTop1ByStockCodeOrderByDateDesc(String symbol);

    @Query(value = """
        SELECT * FROM daily_candle 
        WHERE stock_code = :stockCode
        ORDER BY date DESC
        LIMIT 201
        """, nativeQuery = true)
    List<DailyCandleEntity> findRecent200ByStockCode(@Param("stockCode") String stockCode);
}
