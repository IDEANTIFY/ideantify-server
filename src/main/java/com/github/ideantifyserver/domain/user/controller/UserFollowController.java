package com.github.ideantifyserver.domain.user.controller;

import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.service.UserService;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import com.github.ideantifyserver.global.util.DomainParameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[User - Follow]")
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserFollowController {

    private final UserService userService;

    @PostMapping("/{user}/following")
    @Operation(summary = "팔로우")
    public ApiResponse<Void> followUser(
            @CurrentUser User me,
            @DomainParameter(description = "팔로우할 유저 ID") @PathVariable User user
    ) {

        userService.followUser(me, user);
        return ApiResponse.ok();
    }
}
