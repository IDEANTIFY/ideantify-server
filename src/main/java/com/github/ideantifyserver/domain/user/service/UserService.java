package com.github.ideantifyserver.domain.user.service;

import com.github.ideantifyserver.domain.auth.exception.AuthExceptions;
import com.github.ideantifyserver.domain.user.dto.response.UserResponse;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(User user) {

        return UserResponse.from(userRepository.findById(user.getId()).orElseThrow(AuthExceptions.USER_NOT_FOUND::toException));
    }
}
