package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
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
            @RequestParam(required = false) UUID userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(innerProjectService.getProjectList(bookmark, like, own, userId, pageable, user));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 조회")
    public ApiResponse<ProjectDetailResponseDto> getProject(@PathVariable UUID projectId) {
        return ApiResponse.ok(innerProjectService.getProject(projectId));
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
}
