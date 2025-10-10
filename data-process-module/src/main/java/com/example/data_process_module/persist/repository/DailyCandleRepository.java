package com.example.data_process_module.persist.repository;


import com.example.data_process_module.persist.entity.DailyCandleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCandleRepository extends JpaRepository<DailyCandleEntity, DailyCandleEntity.PK> {
    List<DailyCandleEntity> findTop200ByStockCodeOrderByDateAsc(String stockCode);

    DailyCandleEntity findTop1ByStockCodeOrderByDateDesc(String symbol);
}
