package com.github.ideantifyserver.domain.user.service;

import com.github.ideantifyserver.domain.auth.exception.AuthExceptions;
import com.github.ideantifyserver.domain.user.dto.request.UpdateProfileRequest;
import com.github.ideantifyserver.domain.user.dto.response.SimpleUserResponse;
import com.github.ideantifyserver.domain.user.dto.response.UserResponse;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.exception.UserExceptions;
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

        List<User> users = userRepository.findByNicknameOrEmail(query, Limit.of(5));
        return users.stream().map(SimpleUserResponse::from).toList();
    }

    @Transactional
    public UserResponse updateMyProfile(User user, UpdateProfileRequest request) {

        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.findByNickname(request.getNickname()).isPresent()) {
                throw UserExceptions.ALREADY_EXIST.toException();
            }
            user.updateNickname(request.getNickname());
        }

        if (request.getAvatar() != null) {
            user.updateAvatar(request.getAvatar());
        }

        if (user.getSocial() != null && request.getProfile() != null) {

            user.getSocial().updateSocialLinks(
                    request.getProfile().getGithub(),
                    request.getProfile().getLinkedin(),
                    request.getProfile().getInstagram()
            );
        }

        return UserResponse.from(userRepository.save(user));
    }
}
