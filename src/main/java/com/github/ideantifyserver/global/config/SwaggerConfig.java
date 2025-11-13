package com.github.ideantifyserver.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Server server = new Server()
                .url("https://api.ideantify.wink.io.kr/api")
                .description("Production Server");

        return new OpenAPI()
                .servers(List.of(server))
                .components(new Components()
                        .addSecuritySchemes("JWT", securityScheme()))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .info(info());
    }

    private Info info() {
        return new Info()
                .title("IDEANTIFY")
                .description("아이디어 검증 플랫폼")
                .version("0.0.0");
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
