package com.github.ideantifyserver.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "메시지 전송 응답")
public class UserChatSendResponseDto {

    @Schema(description = "메시지 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "메시지 내용", example = "이 아이디어는 어때?")
    private String content;

    @Schema(description = "WebSocket 구독 토픽 (AI 응답 수신용)",
            example = "/topic/chatRooms/550e8400-e29b-41d4-a716-446655440000")
    private String websocketTopic;

    public static UserChatSendResponseDto of(UUID chatRoomId, UUID messageId, String content) {
        return UserChatSendResponseDto.builder()
                .id(messageId)
                .content(content)
                .websocketTopic("/topic/chatRooms/" + chatRoomId)
                .build();
    }
}
