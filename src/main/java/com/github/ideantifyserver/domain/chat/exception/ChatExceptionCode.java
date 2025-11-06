package com.github.ideantifyserver.domain.chat.exception;

import com.github.ideantifyserver.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatExceptionCode implements ApiExceptionCode {
    CHAT_ROOM_NOT_FOUND("CHAT_001","채팅방이 존재하지 않습니다"),
    CHAT_ACCESS_DENIED("CHAT_002","채팅 권한이 없습니다"),
    SQS_MESSAGE_SEND_FAILED("CHAT_003","AI 메시지 전송에 실패했습니다"),
    SQS_MESSAGE_PARSE_FAILED("CHAT_004","AI 응답 처리에 실패했습니다"),
    CHAT_ROOM_ID_MISMATCH("CHAT_005","채팅방 ID가 일치하지 않습니다")
    ;

    private final String code;
    private final String message;
}
