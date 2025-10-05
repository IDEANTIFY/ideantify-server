package com.github.ideantifyserver.domain.user.dto.response;

import com.github.ideantifyserver.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class SimpleUserResponse {

    UUID id;
    String email;
    String nickname;
    String avatar;

    public static SimpleUserResponse from(User user) {

        return SimpleUserResponse.of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatar()
        );
    }
}
