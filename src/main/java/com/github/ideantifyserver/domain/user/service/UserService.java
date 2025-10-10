package com.github.ideantifyserver.domain.user.service;

import com.github.ideantifyserver.domain.auth.exception.AuthExceptions;
import com.github.ideantifyserver.domain.user.dto.response.SimpleUserResponse;
import com.github.ideantifyserver.domain.user.dto.response.UserResponse;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.entity.UserFollow;
import com.github.ideantifyserver.domain.user.repository.UserFollowRepository;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import com.github.ideantifyserver.global.exception.GlobalExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

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
    @PreAuthorize("#me != #user")
    public void followUser(User me, User user) {

        if (userFollowRepository.existsByFollowerAndFollowing(me, user)) {
            throw GlobalExceptions.NOT_PERMITTED.toException();
        }

        userFollowRepository.save(UserFollow.builder().follower(me).following(user).build());
    }

    @Transactional
    public void unfollowUser(User me, User user) {

        UserFollow userFollow = userFollowRepository.findByFollowerAndFollowing(me, user)
                .orElseThrow(GlobalExceptions.NOT_PERMITTED::toException);

        userFollowRepository.delete(userFollow);
    }
}
