package com.github.ideantifyserver.global.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.property.JwtProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperty jwtProperty;

    @Bean
    public Algorithm algorithm() {

        return Algorithm.HMAC256(jwtProperty.getKey());
    }

    public boolean validateToken(String token) {

        if (Objects.isNull(token)) return false;

        try {

            JWT.require(algorithm()).build().verify(token);
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public UUID extractId(String token) {

        return UUID.fromString(JWT.require(algorithm())
                .build()
                .verify(token).getClaim("id")
                .asString());
    }

    public String generateToken(User user) {

        return JWT.create().withIssuedAt(Instant.now()).withClaim("id", user.getId().toString()).sign(algorithm());
    }
}
