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
    NOT_FOUND("INNERPROJECT_004", "해당 프로젝트를 찾을 수 없습니다."),
    UNAUTHORIZED("INNERPROJECT_005", "가입되지 않은 사용자입니다."),
    OWNER_NOT_FOUND("INNERPROJECT_006", "작성자를 찾을 수 없습니다."),
    NOT_OWNER("INNERPROJECT_007", "해당 프로젝트의 작성자가 아닙니다."),
    COMMENT_PARENT_NOT_FOUND("INNERPROJECT_012", "부모 댓글을 찾을 수 없습니다."),
    ;

    private final String code;
    private final String message;
}
