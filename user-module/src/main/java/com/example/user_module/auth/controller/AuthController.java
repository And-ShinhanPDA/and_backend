package com.example.user_module.auth.controller;


import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.auth.dto.request.AuthReq;
import com.example.user_module.auth.dto.response.AuthRes;
import com.example.user_module.auth.service.AuthService;
import com.example.user_module.common.security.AuthUser;
import com.example.user_module.common.security.jwt.service.RefreshTokenService;
import com.example.user_module.fcm.service.FcmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final FcmService fcmService;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthReq.signUpReq signUpReq) {
        return ResponseEntity.created(URI.create("/login"))
                .body(ApiResponse.success(
                        ResponseCode.SUCCESS_SIGN_UP, authService.signUp(signUpReq)));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthReq.loginReq loginReq) {
        AuthRes.loginRes loginData = authService.login(loginReq);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", loginData.refreshToken())
                .maxAge(14 * 24 * 60 * 60)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(
                        ResponseCode.SUCCESS_LOGIN,
                        Map.of(
                                "accessToken", loginData.accessToken(),
                                "refreshTokenId", loginData.refreshTokenId()
                        )
                ));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@AuthUser Long userId, @RequestBody AuthReq.logoutReq logoutReq) {
        fcmService.deactivateFcmToken(userId, logoutReq.deviceId());
        refreshTokenService.delete(logoutReq.refreshTokenId());
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS_LOGOUT, "로그아웃(기기별) 완료"));
    }


}
