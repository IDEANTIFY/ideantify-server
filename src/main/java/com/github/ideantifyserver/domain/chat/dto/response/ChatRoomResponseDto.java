package com.github.ideantifyserver.domain.chat.dto.response;

import com.github.ideantifyserver.domain.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private UUID chatRoomId;
    private String title;
    private ChatRoom.ChatRoomType type;
    private UUID ideaReportId;  // IDEA_REPORT 타입인 경우만 존재
    private String createdAt;

    public static ChatRoomResponseDto of(UUID chatRoomId, String title, String createdAt) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoomId)
                .title(title)
                .createdAt(createdAt)
                .build();
    }

    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .title(chatRoom.getTitle())
                .type(chatRoom.getType())
                .ideaReportId(chatRoom.getIdeaReport() != null ? chatRoom.getIdeaReport().getId() : null)
                .createdAt(chatRoom.getCreatedAt().toString())
                .build();
    }
}
