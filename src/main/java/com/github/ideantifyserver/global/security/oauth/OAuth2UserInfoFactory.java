package com.github.ideantifyserver.global.security.oauth;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;


public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {

        return switch (provider) {
            case "google" -> new OAuth2GoogleUser(attributes);
            case "kakao" -> new OAuth2KakaoUser(attributes);
            default -> throw new OAuth2AuthenticationException("INVALID PROVIDER TYPE");
        };
    }
}
