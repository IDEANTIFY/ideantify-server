package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.request.CreateCommentRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.*;
import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.project.dto.response.CreatedCommentResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectBookmarkResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectDetailResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectLikeResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectListResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectResponseDto;
import com.github.ideantifyserver.domain.project.service.InnerProjectService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import com.github.ideantifyserver.global.util.DomainParameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[Project]")
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class InnerProjectController {

    private final InnerProjectService innerProjectService;

    @PostMapping
    @Operation(summary = "프로젝트 등록")
    public ApiResponse<ProjectResponseDto> createProject(
            @RequestBody @Valid CreateProjectRequestDto req,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.create(req, user));
    }

    @GetMapping("/list")
    @Operation(summary = "전체 프로젝트 목록 조회")
    public ApiResponse<List<ProjectListResponseDto>> getProjectList(
            @RequestParam(defaultValue = "false") boolean bookmark,
            @RequestParam(defaultValue = "false") boolean like,
            @RequestParam(defaultValue = "false") boolean own,
            @DomainParameter(description = "타겟 유저 ID") @RequestParam(required = false) User user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @CurrentUser User me
    ) {
        return ApiResponse.ok(innerProjectService.getProjectList(bookmark, like, own, user, pageable, me));
    }

    @GetMapping("/{project}")
    @Operation(summary = "프로젝트 조회")
    public ApiResponse<ProjectDetailResponseDto> getProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project
    ) {
        return ApiResponse.ok(innerProjectService.getProject(project));
    }

    @PutMapping("/{project}")
    @Operation(summary = "프로젝트 수정")
    public ApiResponse<ProjectResponseDto> updateProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @RequestBody @Valid UpdateProjectRequestDto req,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.update(project, req, user));
    }

    @DeleteMapping("/{project}")
    @Operation(summary = "프로젝트 삭제")
    public ApiResponse<Void> deleteProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @CurrentUser User user
    ) {
        innerProjectService.delete(project, user);
        return ApiResponse.ok();
    }

    @PostMapping("/{projectId}/comments")
    @Operation(summary = "프로젝트 댓글 작성")
    public ApiResponse<CreatedCommentResponseDto> addComment(
            @PathVariable UUID projectId,
            @RequestParam(required = false, name = "parent") UUID parentId,
            @RequestBody @Valid CreateCommentRequestDto req,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(innerProjectService.addComment(projectId, parentId, req, user));
    }

    @PutMapping("/{projectId}/comments/{commentId}")
    @Operation(summary = "프로젝트 댓글 수정")
    public ApiResponse<CreatedCommentResponseDto> updateComment(
            @PathVariable UUID projectId,
            @PathVariable UUID commentId,
            @RequestBody @Valid CreateCommentRequestDto req,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(innerProjectService.updateComment(projectId, commentId, req, user));
    }

    @DeleteMapping("/{projectId}/comments/{commentId}")
    @Operation(summary = "프로젝트 댓글 삭제")
    public ApiResponse<Void> deleteComment(
            @PathVariable UUID projectId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal User user
    ) {
        innerProjectService.deleteComment(projectId, commentId, user);
        return ApiResponse.ok();
    }
}
