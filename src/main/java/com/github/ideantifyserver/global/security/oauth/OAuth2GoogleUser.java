package com.github.ideantifyserver.global.security.oauth;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class OAuth2GoogleUser implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {

        return "google";
    }

    @Override
    public String getProviderId() {

        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {

        return (String) attributes.get("email");
    }

    @Override
    public String getNickname() {

        return (String) attributes.get("name");
    }

    @Override
    public String getAvatar() {

        return (String) attributes.get("picture");
    }
}
