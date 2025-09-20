package com.github.ideantifyserver.domain.project.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InnerProjectExceptions implements ApiExceptionCode {
    INVALID_KEYWORD("INNERPROJECT_001", "키워드가 올바르지 않습니다."),
    INVALID_FILE_PATH("INNERPROJECT_002", "파일 경로가 올바르지 않습니다."),
    INVALID_MEMBER_ID("INNERPROJECT_003", "유효하지 않은 멤버 ID입니다."),
    ;

    private final String code;
    private final String message;
}
