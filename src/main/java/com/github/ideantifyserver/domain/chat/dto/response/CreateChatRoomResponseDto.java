package com.github.ideantifyserver.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "채팅방 생성 응답")
public class CreateChatRoomResponseDto {

    @Schema(description = "채팅방 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    private String title;

    @Schema(description = "WebSocket 구독 토픽",
            example = "/topic/chatRooms/550e8400-e29b-41d4-a716-446655440000")
    private String websocketTopic;

    public static CreateChatRoomResponseDto of(UUID id, String title) {
        return CreateChatRoomResponseDto.builder()
                .id(id)
                .title(title)
                .websocketTopic("/topic/chatRooms/" + id)
                .build();
    }
}
