package com.github.ideantifyserver.global.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperty {

    @NotBlank
    String host;

    @NotNull
    Integer port;

    String password;
}