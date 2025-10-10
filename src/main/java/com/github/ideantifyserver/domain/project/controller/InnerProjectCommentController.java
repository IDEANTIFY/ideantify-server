package com.github.ideantifyserver.domain.project.controller;

import com.github.ideantifyserver.domain.project.dto.response.CreatedCommentResponseDto;
import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.project.entity.InnerProjectComment;
import com.github.ideantifyserver.domain.project.service.InnerProjectService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import com.github.ideantifyserver.global.util.DomainParameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "[Project - Comment]")
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class InnerProjectCommentController {

    private final InnerProjectService innerProjectService;

    @PostMapping("/{project}/comments")
    @Operation(summary = "프로젝트 댓글 작성")
    public ApiResponse<CreatedCommentResponseDto> addComment(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @RequestParam(required = false, name = "parent") UUID parentId,
            @RequestBody @NotBlank String content,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.addComment(project, parentId, content, user));
    }

    @PutMapping("/{project}/comments/{comment}")
    @Operation(summary = "프로젝트 댓글 수정")
    public ApiResponse<CreatedCommentResponseDto> updateComment(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @DomainParameter(description = "댓글 ID") @PathVariable InnerProjectComment comment,
            @RequestBody @NotBlank String content,
            @CurrentUser User user
    ) {
        return ApiResponse.ok(innerProjectService.updateComment(project, comment, content, user));
    }

    @DeleteMapping("/{project}/comments/{comment}")
    @Operation(summary = "프로젝트 댓글 삭제")
    public ApiResponse<Void> deleteComment(
            @DomainParameter(description = "프로젝트 ID") @PathVariable InnerProject project,
            @DomainParameter(description = "댓글 ID") @PathVariable InnerProjectComment comment,
            @CurrentUser User user
    ) {
        innerProjectService.deleteComment(project, comment, user);
        return ApiResponse.ok();
    }
}
