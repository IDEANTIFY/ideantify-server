package com.github.ideantifyserver.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class ChatRoomListResponseDto {
    List<ChatRoomResponseDto> chatRooms;
}
