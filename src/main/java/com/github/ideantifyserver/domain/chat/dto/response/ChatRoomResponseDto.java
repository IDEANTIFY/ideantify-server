package com.github.ideantifyserver.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class ChatRoomResponseDto {
    private UUID chatRoomId;
    private String title;
    private String createdAt;
}
