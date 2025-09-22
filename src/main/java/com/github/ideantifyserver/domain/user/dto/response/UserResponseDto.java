package com.github.ideantifyserver.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class UserResponseDto {
    UUID id;
    String nickname;
    String avatar;
}
