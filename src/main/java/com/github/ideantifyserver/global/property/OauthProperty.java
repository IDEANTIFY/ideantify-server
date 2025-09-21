package com.github.ideantifyserver.global.property;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.auth.oauth.callback")
public class OauthProperty {

    @NotBlank
    private String google;

    @NotBlank
    private String kakao;
}
