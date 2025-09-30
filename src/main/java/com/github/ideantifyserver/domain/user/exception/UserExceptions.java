package com.github.ideantifyserver.domain.user.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserExceptions implements ApiExceptionCode {

    NOT_FOUND("USER_001", "유저를 찾을 수 없습니다."),
    ;

    private final String code;
    private final String message;
}
