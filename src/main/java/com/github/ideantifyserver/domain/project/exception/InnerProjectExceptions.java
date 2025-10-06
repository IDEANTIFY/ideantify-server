package com.github.ideantifyserver.domain.project.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InnerProjectExceptions implements ApiExceptionCode {

    ALREADY_LIKED("INNER_PROJECT_001", "이미 좋아요를 누른 프로젝트입니다."),
    NOT_LIKED("INNER_PROJECT_002", "좋아요를 누르지 않은 프로젝트입니다."),
    ALREADY_BOOKMARKED("INNER_PROJECT_003", "이미 북마크한 프로젝트입니다."),
    NOT_BOOKMARKED("INNER_PROJECT_004", "북마크하지 않은 프로젝트입니다."),
    ;

    private final String code;
    private final String message;
}
