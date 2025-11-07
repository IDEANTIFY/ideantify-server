package com.github.ideantifyserver.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class CreateChatRoomResponseDto {
    private UUID id;
    private String title;
}
