package com.github.ideantifyserver.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequestMessage {
    private UUID chatRoomId;
    private UUID userId;
    private UUID ideaReportId;
    private IdeaReportData ideaReportData;
    private String content;
}
