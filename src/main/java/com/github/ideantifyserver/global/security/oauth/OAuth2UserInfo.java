package com.github.ideantifyserver.global.security.oauth;

public interface OAuth2UserInfo {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getNickname();
    String getAvatar();
}
