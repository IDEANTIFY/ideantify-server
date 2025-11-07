package com.github.ideantifyserver.domain.keyword.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeywordExceptions implements ApiExceptionCode {

    NOT_FOUND("KEYWORD_001", "존재하지 않는 키워드입니다."),
    INVALID_KEYWORD_COUNT("KEYWORD_002", "키워드는 최소 1개 이상 선택해야 합니다."),
    ALREADY_SELECTED("KEYWORD_003", "이미 키워드를 선택했습니다. 키워드는 변경할 수 없습니다.")
    ;

    private final String code;
    private final String message;
}