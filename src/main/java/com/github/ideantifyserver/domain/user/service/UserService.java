package com.github.ideantifyserver.domain.user.service;

import com.github.ideantifyserver.domain.user.dto.response.UserResponse;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import com.github.ideantifyserver.global.security.oauth.CustomOAuth2UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(CustomOAuth2UserDetails user) {

        return UserResponse.from(userRepository.findById(user.getId()).orElseThrow());
    }
}
