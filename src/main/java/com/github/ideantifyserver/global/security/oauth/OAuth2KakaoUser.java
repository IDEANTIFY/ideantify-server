package com.github.ideantifyserver.global.security.oauth;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class OAuth2KakaoUser implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "";
    }

    @Override
    public String getProviderId() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public String getNickname() {
        return "";
    }

    @Override
    public String getAvatar() {
        return "";
    }
}
