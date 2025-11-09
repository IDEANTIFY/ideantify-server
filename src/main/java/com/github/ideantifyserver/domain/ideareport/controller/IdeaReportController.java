package com.github.ideantifyserver.domain.ideareport.controller;

import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportMetadataRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.response.*;
import com.github.ideantifyserver.domain.ideareport.service.IdeaReportService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/idea-reports")
@RequiredArgsConstructor
@Tag(name = "[IdeaReport]")
public class IdeaReportController {

    private final IdeaReportService ideaReportService;

    @PostMapping("/metadata")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    description = "웹소켓 토픽: /topic/idea-reports/metadata/{jobId}",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JobIdWebsocketTopicResponseDto.class)
                            ),
                            @Content(
                                    mediaType = "application/vnd.ws-push+json",
                                    schema = @Schema(implementation = CreateIdeaReportMetadataResponseDto.class)
                            )
                    }
            )
    })
    public ApiResponse<JobIdWebsocketTopicResponseDto> createIdeaReportMetadata(@RequestBody @Valid CreateIdeaReportMetadataRequestDto requestDto) {
        UUID id = ideaReportService.createMetadata(requestDto);
        return ApiResponse.ok(
                JobIdWebsocketTopicResponseDto.of(
                        id,
                        "/topic/idea-reports/metadata/" + id.toString()
                )
        );
    }

    @PostMapping
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    description = "웹소켓 토픽: /topic/idea-reports/{jobId}",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JobIdWebsocketTopicResponseDto.class)
                            ),
                            @Content(
                                    mediaType = "application/vnd.ws-push+json",
                                    schema = @Schema(implementation = IdeaReportResponseDto.class)
                            )
                    }
            )
    })
    public ApiResponse<JobIdWebsocketTopicResponseDto> createIdeaReport(
            @RequestBody @Valid CreateIdeaReportRequestDto requestDto,
            @CurrentUser(required = false) User user
    ) {
        UUID id = ideaReportService.createIdeaReport(requestDto, user);
        return ApiResponse.ok(JobIdWebsocketTopicResponseDto.of(
                id,
                "/topic/idea-reports/" + id.toString()
        ));
    }

    @GetMapping("/results")
    public ApiResponse<List<IdeaReportListResponseDto>> getIdeaReportList(@CurrentUser(required = false) User user) {
        return ApiResponse.ok(ideaReportService.getIdeaReportList(user));
    }

    @GetMapping("/results/{resultId}")
    public ApiResponse<IdeaReportResultDetailResponseDto> getIdeaReportResult(
            @PathVariable UUID resultId, @CurrentUser(required = false) User user
    ) {
        return ApiResponse.ok(ideaReportService.getIdeaReportResult(resultId, user));
    }
}
