package com.github.ideantifyserver.global.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperty {

    @Size(min = 32)
    @NotBlank
    String key;
}
