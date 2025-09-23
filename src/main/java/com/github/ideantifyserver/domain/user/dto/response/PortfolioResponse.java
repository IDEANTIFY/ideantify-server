package com.github.ideantifyserver.domain.user.dto.response;

import com.github.ideantifyserver.domain.user.entity.UserSocial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(name = "포트폴리오 응답 DTO")
@AllArgsConstructor(staticName = "of")
public class PortfolioResponse {

    String github;

    String linkedin;

    String instagram;

    public static PortfolioResponse from(UserSocial social) {

        return PortfolioResponse.of(
                social.getGithub(),
                social.getLinkedin(),
                social.getInstagram()
        );
    }
}
