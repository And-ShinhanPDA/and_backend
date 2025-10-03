package com.example.data_process_module.persist.service;

import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.repository.DailyCandleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersistService {

    private final DailyCandleRepository dailyRepo;

    public void saveDaily(DailyCandleEntity entity) {
//        dailyRepo.save(entity);
        log.info("일별 데이터 저장됐다치자");
    }
}

