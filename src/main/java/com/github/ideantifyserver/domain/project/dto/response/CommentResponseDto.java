package com.github.ideantifyserver.domain.project.dto.response;

import com.github.ideantifyserver.domain.project.entity.InnerProjectComment;
import com.github.ideantifyserver.domain.user.dto.response.SimpleUserResponse;
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
    SimpleUserResponse user;
    String content;
    boolean isDeleted;
    List<CommentResponseDto> comments;

    public static CommentResponseDto from(InnerProjectComment comment) {

        return CommentResponseDto.of(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                SimpleUserResponse.of(
                        comment.getUser().getId(),
                        comment.getUser().getEmail(),
                        comment.getUser().getNickname(),
                        comment.getUser().getAvatar()
                ),
                comment.getContent(),
                comment.getChildren().stream()
                        .map(CommentResponseDto::from)
                        .toList()
        );
    }
}
