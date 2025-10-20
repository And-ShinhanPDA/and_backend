package com.example.alert_module.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "alertPrice")
@Entity
@Getter
@Setter
public class AlertPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "stock_code")
    String stockCode;

    @Column(name = "is_price")
    boolean togglePrice;
}
