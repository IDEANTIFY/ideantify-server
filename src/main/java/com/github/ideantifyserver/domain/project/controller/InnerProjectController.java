package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.request.CreateCommentRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.CreatedCommentResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectDetailResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectListResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectResponseDto;
import com.github.ideantifyserver.domain.project.service.InnerProjectService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(innerProjectService.create(req, user));
    }

    @GetMapping("/list")
    @Operation(summary = "전체 프로젝트 목록 조회")
    public ApiResponse<List<ProjectListResponseDto>> getProjectList(
            @RequestParam(defaultValue = "false") boolean bookmark,
            @RequestParam(defaultValue = "false") boolean like,
            @RequestParam(defaultValue = "false") boolean own,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(innerProjectService.getProjectList(bookmark, like, own, pageable, user));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 조회")
    public ApiResponse<ProjectDetailResponseDto> getProject(@PathVariable UUID projectId) {
        return ApiResponse.ok(innerProjectService.getProject(projectId));
    }

    @GetMapping(params = "userId")
    @Operation(summary = "해당 유저의 프로젝트 조회")
    public ApiResponse<List<ProjectListResponseDto>> getAllProjects(
            @RequestParam UUID userId
    ) {
        return ApiResponse.ok(innerProjectService.getProjectsByUser(userId));
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "프로젝트 수정")
    public ApiResponse<ProjectResponseDto> updateProject(
            @PathVariable UUID projectId,
            @RequestBody @Valid UpdateProjectRequestDto req,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(innerProjectService.update(projectId, req, user));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제")
    public ApiResponse<Void> deleteProject(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User user
    ) {
        innerProjectService.delete(projectId, user);
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
