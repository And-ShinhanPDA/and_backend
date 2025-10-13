package com.example.common_service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS_SIGN_UP("SUCCESS_SIGN_UP", "회원가입에 성공하였습니다.", HttpStatus.CREATED),
    SUCCESS_LOGIN("SUCCESS_LOGIN", "로그인에 성공하였습니다.", HttpStatus.OK),
    SUCCESS_LOGOUT("SUCCESS_LOGOUT", "로그아웃에 성공하였습니다.", HttpStatus.OK),
    SUCCESS_REISSUE("SUCCESS_REISSUE", "토큰 재발급에 성공하였습니다.", HttpStatus.OK),

    SUCCESS_TODAY_ALERT("SUCCESS_TODAY_ALERT", "오늘 울린 알림 조회에 성공하였습니다.", HttpStatus.OK),
    SUCCESS_ALERT_HISTORY_ALL("SUCCESS_ALERT_HISTORY_ALL", "알림 히스토리 조회에 성공하였습니다.", HttpStatus.OK),
    SUCCESS_ALERT_HISTORY_PERIOD("SUCCESS_ALERT_HISTORY_PERIOD", "알림 히스토리 특정 기간 조회에 성공하였습니다.", HttpStatus.OK),

    SUCCESS_GET_ALERTED_COMPANIES("SUCCESS_GET_ALERTED_COMPANIES", "알림 설정한 기업 조회에 성공하였습니다.", HttpStatus.OK),
    SUCCESS_GET_ALL_COMPANIES("SUCCESS_GET_ALERTED_COMPANIES", "전체 기업 조회에 성공하였습니다.", HttpStatus.OK),
    SUCCESS_DELETE_ALERT_COMPANY("SUCCESS_DELETE_ALERT_COMPANY", "알림 전체 삭제에 성공하였습니다.", HttpStatus.OK),
    SUCCESS_TOGGLE_ALERT_COMPANY("SUCCESS_TOGGLE_ALERT_COMPANY", "알림 전체 활성화/비활성화 성공하였습니다.", HttpStatus.OK),
    SUCCESS_ALERT_HEATMAP("SUCCESS_ALERT_HEATMAP", "일주일동안 울린 알림 조회에 성공하였습니다.", HttpStatus.OK),
    SUCCESS_GET_DAILY_CANDLE("SUCCESS_GET_DAILY_CANDLE", "일봉 데이터 조회에 성공하였습니다.", HttpStatus.OK),

    USER_EXIST("USER_EXIST", "이미 존재하는 회원이 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("INVALID_INPUT", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    LOGIN_FAIL("LOGIN_FAIL", "이메일 또는 비밀번호가 틀렸습니다", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "권한이 없습니다.", HttpStatus.FORBIDDEN),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", "토큰이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    EXPIRED_REFRESH_TOKEN("EXPIRED_REFRESH_TOKEN", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

    STOCK_NOT_FOUND("STOCK_NOT_FOUND", "해당하는 종목이 없습니다.", HttpStatus.NOT_FOUND),
    NO_EXIST_ALERT("NO_EXIST_ALERT", "삭제할 알림이 없습니다.", HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
