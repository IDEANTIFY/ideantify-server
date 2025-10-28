package com.github.ideantifyserver.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateChatRoomRequestDto {
    @NotBlank
    private String content;
}
