package com.example.user_module.security.jwt;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        if (exception instanceof ErrorCode errorCode) {
            // ErrorCode에 맞는 HttpStatus를 직접 매핑
            HttpStatus status = mapToHttpStatus(errorCode);
            setResponse(response, status, errorCode);
            return;
        }

        // 기본적으로 401 Unauthorized 응답
        setResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
    }

    private void setResponse(HttpServletResponse response,
                             HttpStatus status,
                             ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status.value());

        ApiResponse<Void> errorResponse = ApiResponse.error(
                errorCode.getCode(),
                errorCode.getMessage()
        );

        String errorJson = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(errorJson);
    }

    private HttpStatus mapToHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
        };
    }
}
