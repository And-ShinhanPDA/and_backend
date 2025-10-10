package com.example.alert_module.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "company")
public class Company {

    @Id
    @Column(name = "company_stockcode")
    private String stockCode;

    private String name;

}
