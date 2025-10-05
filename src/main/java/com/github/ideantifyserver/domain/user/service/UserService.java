package com.github.ideantifyserver.domain.user.service;

import com.github.ideantifyserver.domain.auth.exception.AuthExceptions;
import com.github.ideantifyserver.domain.user.dto.response.SimpleUserResponse;
import com.github.ideantifyserver.domain.user.dto.response.UserResponse;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(User user) {

        return UserResponse.from(userRepository.findById(user.getId()).orElseThrow(AuthExceptions.USER_NOT_FOUND::toException));
    }

    @Transactional(readOnly = true)
    public List<SimpleUserResponse> searchUsers(String query) {

        List<User> user = userRepository.findByNicknameOrEmail(query, Limit.of(5));
        return user.stream().map(SimpleUserResponse::from).toList();
    }
}
