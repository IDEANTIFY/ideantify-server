package com.github.ideantifyserver.domain.ideareport.controller;

import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.response.IdeaReportJobIdResponse;
import com.github.ideantifyserver.domain.ideareport.service.IdeaReportService;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
