package com.example.data_process_module.persist.repository;

import com.example.data_process_module.persist.entity.MinuteCandleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinuteCandleRepository extends JpaRepository<MinuteCandleEntity, MinuteCandleEntity.PK> {
}

