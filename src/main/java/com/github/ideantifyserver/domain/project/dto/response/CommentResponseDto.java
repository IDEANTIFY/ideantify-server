package com.github.ideantifyserver.domain.project.dto.response;

import com.github.ideantifyserver.domain.project.entity.InnerProjectComment;
import com.github.ideantifyserver.domain.user.dto.response.SimpleUserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public static CommentResponseDto from(InnerProjectComment comment, Map<UUID, List<InnerProjectComment>> childrenMap) {

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
                comment.isDeleted(),
                toCommentTreeDto(comment, childrenMap)
        );
    }

    private static List<CommentResponseDto> toCommentTreeDto(InnerProjectComment comment, Map<UUID, List<InnerProjectComment>> childrenMap) {

        return childrenMap
                .getOrDefault(comment.getId(), List.of())
                .stream()
                .sorted(Comparator.comparing(InnerProjectComment::getCreatedAt))
                .map(c -> CommentResponseDto.from(c, childrenMap))
                .toList();
    }
}
