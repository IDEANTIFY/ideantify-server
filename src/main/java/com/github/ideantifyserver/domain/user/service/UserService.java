package com.github.ideantifyserver.domain.user.service;

import com.github.ideantifyserver.domain.auth.exception.AuthExceptions;
import com.github.ideantifyserver.domain.user.dto.response.SimpleUserResponse;
import com.github.ideantifyserver.domain.user.dto.response.UserResponse;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.exceptions.UserExceptions;
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

    @Transactional(readOnly = true)
    public SimpleUserResponse searchUsers(String nickname, String email) {

        if (nickname == null || nickname.isEmpty() || email == null || email.isEmpty()) {
            throw UserExceptions.INVALID_REQUEST.toException();
        }

        User user = userRepository.findByNicknameOrEmail(nickname, email).orElseThrow(AuthExceptions.USER_NOT_FOUND::toException);
        return SimpleUserResponse.from(user);
    }
}
