package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.*;
import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.project.service.InnerProjectService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "[Project]")
public class InnerProjectController {

    private final InnerProjectService innerProjectService;

    @PostMapping
    @Operation(summary = "프로젝트 등록")
    public ApiResponse<ProjectResponseDto> createProject(
            @RequestBody @Valid CreateProjectRequestDto req,
            @CurrentUser(required = true) User user
    ) {
        return ApiResponse.ok(innerProjectService.create(req, user));
    }

    @GetMapping("/list")
    @Operation(summary = "전체 프로젝트 목록 조회")
    public ApiResponse<List<ProjectListResponseDto>> getProjectList(
            @RequestParam(defaultValue = "false") boolean bookmark,
            @RequestParam(defaultValue = "false") boolean like,
            @RequestParam(defaultValue = "false") boolean own,
            @RequestParam(required = false) UUID userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @CurrentUser(required = true) User user
    ) {
        return ApiResponse.ok(innerProjectService.getProjectList(bookmark, like, own, userId, pageable, user));
    }

    @GetMapping("/{project}")
    @Operation(summary = "프로젝트 조회")
    public ApiResponse<ProjectDetailResponseDto> getProject(
            @Parameter(description = "프로젝트 ID", schema = @Schema(type = "string", format = "uuid")) @PathVariable InnerProject project
    ) {
        return ApiResponse.ok(innerProjectService.getProject(project));
    }

    @PutMapping("/{project}")
    @Operation(summary = "프로젝트 수정")
    public ApiResponse<ProjectResponseDto> updateProject(
            @Parameter(description = "프로젝트 ID", schema = @Schema(type = "string", format = "uuid")) @PathVariable InnerProject project,
            @RequestBody @Valid UpdateProjectRequestDto req,
            @CurrentUser(required = true) User user
    ) {
        return ApiResponse.ok(innerProjectService.update(project, req, user));
    }

    @DeleteMapping("/{project}")
    @Operation(summary = "프로젝트 삭제")
    public ApiResponse<Void> deleteProject(
            @Parameter(description = "프로젝트 ID", schema = @Schema(type = "string", format = "uuid")) @PathVariable InnerProject project,
            @CurrentUser(required = true) User user
    ) {
        innerProjectService.delete(project, user);
        return ApiResponse.ok();
    }

    @PostMapping("/{project}/bookmark")
    @Operation(summary = "북마크 추가")
    public ApiResponse<ProjectBookmarkResponseDto> bookmarkProject(
            @Parameter(description = "프로젝트 ID", schema = @Schema(type = "string", format = "uuid")) @PathVariable InnerProject project,
            @CurrentUser(required = true) User user
    ) {
        return ApiResponse.ok(innerProjectService.bookmarkProject(project, user));
    }

    @DeleteMapping("/{project}/bookmark")
    @Operation(summary = "북마크 삭제")
    public ApiResponse<ProjectBookmarkResponseDto> unbookmarkProject(
            @Parameter(description = "프로젝트 ID", schema = @Schema(type = "string", format = "uuid")) @PathVariable InnerProject project,
            @CurrentUser(required = true) User user
    ) {
        return ApiResponse.ok(innerProjectService.unbookmarkProject(project, user));
    }

    @PostMapping("/{project}/like")
    @Operation(summary = "프로젝트 좋아요 추가")
    public ApiResponse<ProjectLikeResponseDto> likeProject(
            @Parameter(description = "프로젝트 ID", schema = @Schema(type = "string", format = "uuid")) @PathVariable InnerProject project,
            @CurrentUser(required = true) User user
    ) {
        return ApiResponse.ok(innerProjectService.likeProject(project, user));
    }

    @DeleteMapping("/{project}/like")
    @Operation(summary = "프로젝트 좋아요 삭제")
    public ApiResponse<ProjectLikeResponseDto> unlikeProject(
            @Parameter(description = "프로젝트 ID", schema = @Schema(type = "string", format = "uuid")) @PathVariable InnerProject project,
            @CurrentUser(required = true) User user
    ) {
        return ApiResponse.ok(innerProjectService.unlikeProject(project, user));
    }
}
