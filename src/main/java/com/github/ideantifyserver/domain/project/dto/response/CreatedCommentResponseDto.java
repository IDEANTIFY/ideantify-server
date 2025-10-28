package com.github.ideantifyserver.domain.project.dto.response;

import com.github.ideantifyserver.domain.project.entity.InnerProjectComment;
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

    public static CreatedCommentResponseDto from(InnerProjectComment comment) {

        return CreatedCommentResponseDto.of(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                CommentUserDto.of(
                        comment.getUser().getId(),
                        comment.getUser().getNickname(),
                        comment.getUser().getAvatar()
                ),
                comment.getContent()
        );
    }
}
