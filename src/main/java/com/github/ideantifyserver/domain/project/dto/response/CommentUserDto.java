package com.github.ideantifyserver.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class CommentUserDto {
    UUID id;
    String nickname;
    String avatar;
}
