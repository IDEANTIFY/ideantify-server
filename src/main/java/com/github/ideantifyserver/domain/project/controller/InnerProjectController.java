package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectResponseDto;
import com.github.ideantifyserver.domain.project.service.InnerProjectService;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
