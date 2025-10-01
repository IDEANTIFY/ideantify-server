package com.github.ideantifyserver.domain.auth.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptions implements ApiExceptionCode {

    AUTHENTICATION_FAILED("AUTH_001", "인증에 실패했습니다."),
    USER_NOT_FOUND("AUTH_002", "유저를 찾을 수 없습니다."),
    INVALID_REQUEST("AUTH_003", "유효하지 않은 요청입니다."),
    AUTHENTICATION_REQUIRED("AUTH_004", "인증 정보가 필요합니다.")
    ;

    private final String code;
    private final String message;
}
