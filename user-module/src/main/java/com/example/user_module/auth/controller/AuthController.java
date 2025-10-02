package com.example.user_module.auth.controller;


import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.auth.dto.request.AuthReq;
import com.example.user_module.auth.service.AuthService;
import com.example.user_module.common.security.CustomUserDetails;
import com.example.user_module.common.security.jwt.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null) return null;
        return ((CustomUserDetails) auth.getPrincipal()).user().getId();
    }

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

    @DeleteMapping("/logout/{refreshTokenId}")
    public ResponseEntity<?> logout(@PathVariable UUID refreshTokenId) {
        refreshTokenService.delete(refreshTokenId);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS_LOGOUT, "로그아웃(기기별) 완료"));
    }


}
