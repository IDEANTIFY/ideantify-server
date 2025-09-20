package com.github.ideantifyserver.global.security.oauth;

import lombok.AllArgsConstructor;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class OAuth2KakaoUser implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getProviderId() {

        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {

        return Optional.ofNullable(getKakaoAccount())
                .map(acc -> (String) acc.get("email"))
                .orElse(null);
    }

    @Override
    public String getNickname() {

        return Optional.ofNullable(getProfile())
                .map(profile -> (String) profile.get("nickname"))
                .orElse(null);
    }

    @Override
    public String getAvatar() {

        return Optional.ofNullable(getProfile())
                .map(profile -> (String) profile.get("profile_image_url"))
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoAccount() {

        return (Map<String, Object>) attributes.get("kakao_account");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getProfile() {

        Map<String, Object> kakaoAccount = getKakaoAccount();

        return kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
    }
}
