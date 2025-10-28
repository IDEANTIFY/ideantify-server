package com.github.ideantifyserver.domain.ideareport.controller;

import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportMetadataRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.response.IdeaReportJobIdResponse;
import com.github.ideantifyserver.domain.ideareport.service.IdeaReportService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/idea-reports")
@RequiredArgsConstructor
@Tag(name = "[IdeaReport]")
public class IdeaReportController {

    private final IdeaReportService ideaReportService;

    @PostMapping
    public ApiResponse<IdeaReportJobIdResponse> createIdeaReport(@RequestBody @Valid CreateIdeaReportRequestDto requestDto) {
        return ApiResponse.ok(ideaReportService.createAsync(requestDto));
    }

    @PostMapping("/metadata")
    public ApiResponse<Map<String, Object>> createMetadata(
            @RequestBody @Valid CreateIdeaReportMetadataRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        UUID id = ideaReportService.createIdeaReportMetadata(requestDto, user);
        return ApiResponse.ok(Map.of(
                "id", id,
                "websocketTopic", "/topic/idea-reports/metadata/" + id
        ));
    }
}
