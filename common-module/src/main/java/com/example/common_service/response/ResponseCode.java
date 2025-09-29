package com.example.common_service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS_SIGN_UP("SUCCESS_SIGN_UP", "회원가입에 성공하였습니다.", HttpStatus.CREATED),
    SUCCESS_LOGIN("SUCCESS_LOGIN", "로그인에 성공하였습니다.", HttpStatus.OK),


    USER_EXIST("USER_EXIST", "이미 존재하는 회원이 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("INVALID_INPUT", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),


    LOGIN_FAIL("LOGIN_FAIL", "이메일 또는 비밀번호가 틀렸습니다", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "권한이 없습니다.", HttpStatus.FORBIDDEN),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
