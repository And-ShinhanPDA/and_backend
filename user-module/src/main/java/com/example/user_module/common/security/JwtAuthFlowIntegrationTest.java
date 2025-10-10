package com.example.user_module.common.security;

import com.example.common_service.response.ApiResponse;
import com.example.user_module.auth.dto.request.AuthReq;
import com.example.user_module.auth.entity.UserEntity;
import com.example.user_module.auth.repository.UserRepository;
import com.example.user_module.common.security.jwt.JwtProvider;
import com.example.user_module.common.security.jwt.dto.RefreshReq;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity user;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user = userRepository.save(UserEntity.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("1234"))
                .name("tester")
                .build());
    }

    @Test
    @DisplayName("JWT 인증 전체 흐름 (로그인 → 만료 → 재발급)")
    void jwtAuthAndRefreshFlowWithCookie() throws Exception {
        // 1️⃣ 로그인
        var loginReq = new AuthReq.loginReq(user.getEmail(), "1234");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().httpOnly("refreshToken", true))
                .andReturn();

        var loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<Map<String, String>>>() {}
        );

        Map<String, String> data = loginResponse.getData();
        String accessToken = data.get("accessToken");
        UUID refreshTokenId = UUID.fromString(data.get("refreshTokenId"));
        Cookie refreshTokenCookie = loginResult.getResponse().getCookie("refreshToken");

        assertThat(accessToken).isNotBlank();

        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        String expiredToken = jwtProvider.generateExpiredToken(user.getId());

        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());

        RefreshReq refreshReq = new RefreshReq(refreshTokenId);

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq))
                        .cookie(refreshTokenCookie))
                .andExpect(status().isOk())
                .andReturn();

        var refreshResponse = objectMapper.readValue(
                refreshResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<Map<String, String>>>() {}
        );

        String newAccessToken = refreshResponse.getData().get("accessToken");
        assertThat(newAccessToken).isNotBlank();

        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk());
    }
}
