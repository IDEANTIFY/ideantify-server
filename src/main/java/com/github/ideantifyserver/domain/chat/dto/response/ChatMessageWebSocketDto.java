package com.github.ideantifyserver.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ChatMessageWebSocketDto {
    private UUID chatRoomId;
    private String role;
    private String content;
    private String timestamp;
}
