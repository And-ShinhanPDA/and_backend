package com.example.common_service.response;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FONUD", "존재하지 않는 회원입니다."),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN("FORBIDDEN", "권한이 없습니다.");
    private final String code;
    private final String message;

}
