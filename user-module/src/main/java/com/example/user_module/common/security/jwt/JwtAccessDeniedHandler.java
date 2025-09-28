package com.example.user_module.common.security.jwt;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ApiResponse<Void> errorResponse =
                ApiResponse.error(ResponseCode.FORBIDDEN.getCode(), ResponseCode.FORBIDDEN.getMessage());

        String errorJson = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(errorJson);
    }
}
