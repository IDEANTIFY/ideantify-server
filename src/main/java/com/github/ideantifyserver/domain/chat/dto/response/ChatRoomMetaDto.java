package com.github.ideantifyserver.domain.chat.dto.response;

import com.github.ideantifyserver.domain.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.github.ideantifyserver.domain.chat.entity.ChatRoom.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMetaDto {
    private UUID id;
    private ChatRoomType type;
    private String title;
    private UUID ideaReportId;  // IDEA_REPORT 타입인 경우만

    public static ChatRoomMetaDto from(ChatRoom chatRoom) {
        return ChatRoomMetaDto.builder()
                .id(chatRoom.getId())
                .type(chatRoom.getType())
                .title(chatRoom.getTitle())
                .ideaReportId(chatRoom.getIdeaReport() != null ? chatRoom.getIdeaReport().getId() : null)
                .build();
    }
}
