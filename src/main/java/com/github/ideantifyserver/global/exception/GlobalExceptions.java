package com.github.ideantifyserver.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptions implements ApiExceptionCode {

    NOT_FOUND("GLOBAL_001", "요청하신 리소스를 찾을 수 없습니다."),
    INVALID_REQUEST("GLOBAL_002", "요청 데이터가 올바르지 않습니다."),
    NOT_PERMITTED("GLOBAL_003","권한이 없습니다."),
    EXCEPTION("GLOBAL-010", "알 수 없는 오류가 발생했습니다.")
    ;

    private final String code;
    private final String message;
}
