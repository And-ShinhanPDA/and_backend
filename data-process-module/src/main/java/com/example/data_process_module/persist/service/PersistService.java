package com.example.data_process_module.persist.service;

import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.repository.DailyCandleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersistService {

    private final DailyCandleRepository dailyRepo;

    public void saveDaily(DailyCandleEntity entity) {
        dailyRepo.save(entity);
    }
}

