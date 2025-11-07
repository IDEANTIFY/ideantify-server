package com.github.ideantifyserver.domain.s3.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3Exceptions implements ApiExceptionCode {
    INVALID_CONTENT_TYPE("S3_001", "유효하지 않은 이미지 타입입니다."),
    UPLOAD_FAIL("S3_002", "업로드에 실패하였습니다."),
    INVALID_FILE_EXT("S3_003", "유효하지 않은 파일 확장자입니다."),
    ;
    private final String code;
    private final String message;
}
