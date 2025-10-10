package com.github.ideantifyserver.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(name = "프로필 수정 요청 DTO")
@AllArgsConstructor
public class UpdateProfileRequest {

    String nickname;
    String avatar;
    Profile profile;

    @Data
    @AllArgsConstructor
    public static class Profile {

        String github;
        String linkedin;
        String instagram;
    }
}
