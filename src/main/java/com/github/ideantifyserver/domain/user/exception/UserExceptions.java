package com.github.ideantifyserver.domain.user.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserExceptions implements ApiExceptionCode {

    NO_KEYWORDS_FOUND("USER_003", "유저의 키워드가 없습니다."),
    AI_API_CALL_FAILED("USER_002", "AI API 호출에 실패했습니다.");
    ALREADY_EXIST("USER_001", "이미 사용 중인 닉네임입니다."),
    ;

    private final String code;
    private final String message;
}
