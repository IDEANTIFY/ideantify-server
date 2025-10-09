package com.github.ideantifyserver.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class CreatedCommentResponseDto {
    UUID id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    CommentUserDto user;
    String content;
}
