package com.github.ideantifyserver.domain.user.exceptions;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserExceptions implements ApiExceptionCode {

    INVALID_REQUEST("USER_001", "유효하지 않은 요청입니다."),
    ;

    private final String code;
    private final String message;
}
