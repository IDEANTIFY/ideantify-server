package com.github.ideantifyserver.domain.user.controller;

import com.github.ideantifyserver.domain.user.dto.response.SimpleUserResponse;
import com.github.ideantifyserver.domain.user.dto.response.UserResponse;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.service.UserService;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유저")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(description = "내 정보 조회")
    public ApiResponse<UserResponse> getMyProfile(
            @CurrentUser(required = true) User user
    ) {

        return ApiResponse.ok(userService.getMyProfile(user));
    }

    @GetMapping("/{user}")
    @Operation(description = "다른 사람 정보 조회")
    public ApiResponse<UserResponse> getUserProfile(
            @Parameter(description = "유저 ID", schema = @Schema(type = "string", format = "uuid")) @PathVariable User user
    ) {

        return ApiResponse.ok(userService.getMyProfile(user));
    }

    @GetMapping
    @Operation(description = "유저 검색")
    public ApiResponse<List<SimpleUserResponse>> searchUsers(
            @RequestParam(required = false) String query
    ) {

        return ApiResponse.ok(userService.searchUsers(query));
    }
}
