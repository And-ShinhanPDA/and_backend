package com.example.user_module.auth.controller;


import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.auth.dto.request.AuthReq;
import com.example.user_module.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthReq.signUpReq signUpReq) {
        return ResponseEntity.created(URI.create("/login"))
                .body(ApiResponse.success(
                        ResponseCode.SUCCESS_SIGN_UP, authService.signUp(signUpReq)));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthReq.loginReq loginReq) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        ResponseCode.SUCCESS_LOGIN, authService.login(loginReq)
                ));
    }
}
