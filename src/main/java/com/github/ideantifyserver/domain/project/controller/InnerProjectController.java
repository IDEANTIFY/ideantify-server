package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectDetailResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectListResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectResponseDto;
import com.github.ideantifyserver.domain.project.service.InnerProjectService;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "[Project]")
public class InnerProjectController {

    private final InnerProjectService innerProjectService;

    @PostMapping
    @Operation(summary = "프로젝트 등록")
    public ApiResponse<ProjectResponseDto> createProject(@RequestBody @Valid CreateProjectRequestDto req) {
        return ApiResponse.ok(innerProjectService.create(req));
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
    public ApiResponse<ProjectResponseDto> updateProject(@PathVariable UUID projectId, @RequestBody @Valid UpdateProjectRequestDto req) {
        return ApiResponse.ok(innerProjectService.update(projectId, req));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제")
    public ApiResponse<Void> deleteProject(@PathVariable UUID projectId) {
        innerProjectService.delete(projectId);
        return ApiResponse.ok();
    }
}
