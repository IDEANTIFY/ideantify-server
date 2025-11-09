package com.github.ideantifyserver.domain.user.dto.response;

import com.github.ideantifyserver.domain.keyword.dto.response.KeywordResponse;
import com.github.ideantifyserver.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(name = "유저 응답 DTO")
@AllArgsConstructor(staticName = "of")
public class UserResponse {

    UUID id;

    String nickname;

    String email;

    String avatar;

    Integer followers;

    Integer following;

    Integer projects;

    PortfolioResponse portfolio;

    List<KeywordResponse> keywords;

    public static UserResponse from(User user) {

        return UserResponse.of(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getAvatar(),
                user.getFollowers().size(),
                user.getFollowings().size(),
                user.getProjects().size(),
                PortfolioResponse.from(user.getSocial()),
                user.getKeywords().stream()
                        .map(userDomain -> KeywordResponse.from(userDomain.getKeyword()))
                        .toList()
        );
    }
}
