package com.example.search_module.management.service;


import com.example.search_module.management.repository.AlertConditionManagerRepository;
import com.example.search_module.management.repository.AlertConditionRepository;
import com.example.search_module.management.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{

    private final AlertRepository alertRepository;
    private final AlertConditionRepository alertConditionRepository;
    private final AlertConditionManagerRepository alertConditionManagerRepository;




}
