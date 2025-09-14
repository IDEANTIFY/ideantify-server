package com.github.ideantifyserver.global.security.oauth;

import com.github.ideantifyserver.global.property.OauthProperty;
import com.github.ideantifyserver.global.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final OauthProperty oauthProperty;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
        String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();

        CustomOAuth2UserDetails oAuth2User = (CustomOAuth2UserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(oAuth2User.getUser());

        String redirectUrl = switch (registrationId.toLowerCase()) {
            case "google" -> String.format("%s?token=%s", oauthProperty.getGoogle(), token);
            case "kakao" -> String.format("%s?token=%s", oauthProperty.getKakao(), token);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자: " + registrationId);
        };

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
