package com.github.ideantifyserver.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(name = "프로필 수정 요청 DTO")
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotBlank
    String nickname;

    String avatar;

    @Valid
    Profile profile;

    @Data
    @AllArgsConstructor
    public static class Profile {

        String github;
        String linkedin;
        String instagram;
    }
}
