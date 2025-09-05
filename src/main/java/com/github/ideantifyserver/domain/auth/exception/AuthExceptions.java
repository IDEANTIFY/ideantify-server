package com.github.ideantifyserver.domain.auth.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptions implements ApiExceptionCode {

    AUTHENTICATION_FAILED("AUTH_001", "인증에 실패했습니다."),
    ;

    private final String code;
    private final String message;
}
