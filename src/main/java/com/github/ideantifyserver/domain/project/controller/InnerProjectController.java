package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectDetailResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectListResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectResponseDto;
import com.github.ideantifyserver.domain.project.entity.InnerProject;
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

    // ==================== 프로젝트 생성 ====================

    @PostMapping
    @Operation(summary = "프로젝트 등록")
    public ApiResponse<ProjectResponseDto> createProject(
            @RequestBody @Valid CreateProjectRequestDto req,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.create(req, user));
    }

    // ==================== 프로젝트 조회 ====================

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
    @Operation(summary = "프로젝트 상세 조회")
    public ApiResponse<ProjectDetailResponseDto> getProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project
    ) {
        return ApiResponse.ok(innerProjectService.getProject(project));
    }

    // ==================== 프로젝트 수정 ====================

    @PutMapping("/{project}")
    @Operation(summary = "프로젝트 수정")
    public ApiResponse<ProjectResponseDto> updateProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @RequestBody @Valid UpdateProjectRequestDto req,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.update(project, req, user));
    }

    // ==================== 프로젝트 삭제 ====================

    @DeleteMapping("/{project}")
    @Operation(summary = "프로젝트 삭제")
    public ApiResponse<Void> deleteProject(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @CurrentUser User user
    ) {
        innerProjectService.delete(project, user);
        return ApiResponse.ok();
    }
}
