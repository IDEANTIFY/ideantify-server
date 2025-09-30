package com.github.ideantifyserver.global.security.oauth;

import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.entity.UserDomain;
import com.github.ideantifyserver.domain.user.entity.UserProvider;
import com.github.ideantifyserver.domain.user.entity.UserSocial;
import com.github.ideantifyserver.domain.user.repository.UserDomainRepository;
import com.github.ideantifyserver.domain.user.repository.UserProviderRepository;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import com.github.ideantifyserver.domain.user.repository.UserSocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    private final UserSocialRepository userSocialRepository;
    private final UserDomainRepository userDomainRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. 사용자 정보 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // 2. 제공자별 사용자 정보 추출
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());

        // 3. 사용자 조회 혹은 생성
        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElseGet(() -> createUserWithProvider(oAuth2UserInfo));

        return new CustomOAuth2UserDetails(user, oAuth2User.getAttributes());
    }

    private User createUserWithProvider(OAuth2UserInfo oAuth2UserInfo) {

        User user = userRepository.save(User.builder()
                .nickname(oAuth2UserInfo.getNickname())
                .email(oAuth2UserInfo.getEmail())
                .avatar(oAuth2UserInfo.getAvatar())
                .build());

        userProviderRepository.save(UserProvider.builder()
                .providerId(oAuth2UserInfo.getProviderId())
                .provider(UserProvider.Provider.valueOf(oAuth2UserInfo.getProvider().toUpperCase()))
                .user(user)
                .build());

        userSocialRepository.save(UserSocial.builder().user(user).build());

        userDomainRepository.save(UserDomain.builder().user(user).build());

        return user;
    }
}
