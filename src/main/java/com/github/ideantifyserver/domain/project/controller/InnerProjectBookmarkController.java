package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.response.ProjectBookmarkResponseDto;
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

@Tag(name = "[Project - Bookmark]")
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class InnerProjectBookmarkController {

    private final InnerProjectService innerProjectService;

    // ==================== 북마크 ====================

    @PostMapping("/{project}/bookmark")
    @Operation(summary = "북마크 추가")
    public ApiResponse<ProjectBookmarkResponseDto> bookmarkProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.bookmarkProject(project, user));
    }

    @DeleteMapping("/{project}/bookmark")
    @Operation(summary = "북마크 삭제")
    public ApiResponse<ProjectBookmarkResponseDto> unbookmarkProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.unbookmarkProject(project, user));
    }
}
