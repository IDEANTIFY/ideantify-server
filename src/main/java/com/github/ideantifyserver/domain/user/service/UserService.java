package com.github.ideantifyserver.domain.user.service;

import com.github.ideantifyserver.domain.auth.exception.AuthExceptions;
import com.github.ideantifyserver.domain.user.dto.request.UpdateProfileRequest;
import com.github.ideantifyserver.domain.user.dto.response.SimpleUserResponse;
import com.github.ideantifyserver.domain.user.dto.response.TrendingIssueResponse;
import com.github.ideantifyserver.domain.user.dto.response.UserResponse;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.entity.UserFollow;
import com.github.ideantifyserver.domain.user.exception.UserExceptions;
import com.github.ideantifyserver.domain.user.repository.UserFollowRepository;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import com.github.ideantifyserver.global.exception.GlobalExceptions;
import com.github.ideantifyserver.global.infra.ai.service.AiIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    private final AiIssueService aiIssueService;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(User user) {

        return UserResponse.from(userRepository.findById(user.getId()).orElseThrow(AuthExceptions.USER_NOT_FOUND::toException));
    }

    @Transactional(readOnly = true)
    public List<SimpleUserResponse> searchUsers(String query) {

        List<User> users = userRepository.findByNicknameOrEmail(query, Limit.of(5));
        return users.stream().map(SimpleUserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TrendingIssueResponse> getTrendingIssues(User user) {

        User userWithKeywords = userRepository.findById(user.getId())
                .orElseThrow(AuthExceptions.USER_NOT_FOUND::toException);

        List<String> keywords = userWithKeywords.getKeywords().stream()
                .map(userDomain -> userDomain.getKeyword().getName())
                .toList();

        if (keywords.isEmpty()) {
            throw UserExceptions.NO_KEYWORDS_FOUND.toException();
        }

        CompletableFuture<List<TrendingIssueResponse>> future =
                aiIssueService.fetchTrendingIssuesAsync(keywords);

        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("AI 트렌드 이슈 요청 실패", e);
            return List.of();
        }
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
    @PreAuthorize("#me != #user")
    public void unfollowUser(User me, User user) {

        UserFollow userFollow = userFollowRepository.findByFollowerAndFollowing(me, user)
                .orElseThrow(GlobalExceptions.NOT_PERMITTED::toException);

        userFollowRepository.delete(userFollow);
    }

    @Transactional
    public List<SimpleUserResponse> getFollowers(User me) {

        List<UserFollow> follows = userFollowRepository.findByFollowing(me);

        return follows.stream()
                .map(UserFollow::getFollower)
                .map(SimpleUserResponse::from)
                .toList();
    }

    @Transactional
    public List<SimpleUserResponse> getFollowings(User me) {

        List<UserFollow> followings = userFollowRepository.findByFollower(me);

        return followings.stream()
                .map(UserFollow::getFollowing)
                .map(SimpleUserResponse::from)
                .toList();
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
