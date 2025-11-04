package com.github.ideantifyserver.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageListResponseDto {
    private ChatRoomMetaDto chatRoom;  // 채팅방 메타데이터
    private List<ChatMessageDto> messages;

    public static ChatMessageListResponseDto of(ChatRoomMetaDto chatRoom, List<ChatMessageDto> messages) {
        return ChatMessageListResponseDto.builder()
                .chatRoom(chatRoom)
                .messages(messages)
                .build();
    }
}
