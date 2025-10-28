package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.response.ProjectLikeResponseDto;
import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.project.service.InnerProjectService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import com.github.ideantifyserver.global.util.DomainParameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[Project - Like]")
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class InnerProjectLikeController {

    private final InnerProjectService innerProjectService;

    @PostMapping("/{project}/like")
    @Operation(summary = "프로젝트 좋아요 추가")
    public ApiResponse<ProjectLikeResponseDto> likeProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.likeProject(project, user));
    }

    @DeleteMapping("/{project}/like")
    @Operation(summary = "프로젝트 좋아요 삭제")
    public ApiResponse<ProjectLikeResponseDto> unlikeProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.unlikeProject(project, user));
    }
}
