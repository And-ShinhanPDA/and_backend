package com.example.alert_module.management.repository;

import com.example.alert_module.management.dto.CompanyRes;
import com.example.alert_module.management.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query("SELECT new com.example.alert_module.management.dto.CompanyRes(c.stockCode, c.name) FROM Company c")
    List<CompanyRes> findAllCompanies();

    Optional<Company> findByStockCode(String stockCode);


}
