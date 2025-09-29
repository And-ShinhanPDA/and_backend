package com.example.user_module.common.security.jwt;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object exception = request.getAttribute("exception");

        ResponseCode responseCode;

        if (exception instanceof ResponseCode rc) {
            responseCode = rc;
        } else {
            responseCode = ResponseCode.UNAUTHORIZED; // 기본값
        }

        setResponse(response, responseCode);
    }

    private void setResponse(HttpServletResponse response,
                             ResponseCode responseCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(responseCode.getHttpStatus().value());

        ApiResponse<Void> errorResponse = ApiResponse.error(
                responseCode
        );

        String errorJson = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(errorJson);
    }
}
