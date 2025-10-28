package com.github.ideantifyserver.domain.chat.dto.response;

import com.github.ideantifyserver.domain.chat.entity.ChatBot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ChatMessageDto {
    private UUID id;
    private LocalDateTime createdAt;
    private String role;
    private String content;

    public static ChatMessageDto from(ChatBot chatBot) {
        return ChatMessageDto.builder()
                .id(chatBot.getId())
                .createdAt(chatBot.getCreatedAt())
                .role(chatBot.getRole().name())
                .content(chatBot.getContent())
                .build();
    }
}
