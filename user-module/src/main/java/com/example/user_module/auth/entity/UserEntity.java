package com.example.user_module.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor(access =  PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String password;

    private String name;

    private LocalDateTime createdAt;
}
