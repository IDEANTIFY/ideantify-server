package com.github.ideantifyserver.domain.user.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserExceptions implements ApiExceptionCode {

    ALREADY_EXIT("USER_001", "이미 사용 중인 닉네임입니다."),
    ;

    private final String code;
    private final String message;
}
