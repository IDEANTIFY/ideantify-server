package com.github.ideantifyserver.domain.project.dto.response;

import com.github.ideantifyserver.domain.user.dto.response.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class CommentResponseDto {
    UUID id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserResponseDto user;
    String content;
    boolean isDeleted;
    List<CommentResponseDto> comments;
}
