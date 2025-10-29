package com.github.ideantifyserver.domain.ideareport.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdeaReportExceptions implements ApiExceptionCode {
    NOT_FOUND("IDEAREPORT_001", "해당 result id의 idea report result가 없습니다."),
    NOT_OWNER("IDEAREPORT_002", "해당 유저의 result가 아닙니다."),
    ;

    private final String code;
    private final String message;
}
