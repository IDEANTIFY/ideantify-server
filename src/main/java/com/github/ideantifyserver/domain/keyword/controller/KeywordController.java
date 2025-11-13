package com.github.ideantifyserver.domain.keyword.controller;

import com.github.ideantifyserver.domain.keyword.dto.request.SelectKeywordsRequest;
import com.github.ideantifyserver.domain.keyword.dto.response.KeywordResponse;
import com.github.ideantifyserver.domain.keyword.service.KeywordService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@Tag(name = "[Keyword]")
@RestController
@RequestMapping("/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping
    @Operation(summary = "키워드 목록 조회")
    public ApiResponse<List<KeywordResponse>> getAllKeywords() {
        return ApiResponse.ok(keywordService.getAllKeywords());
    }

    @PutMapping("/me")
    @Operation(summary = "키워드 선택")
    public ApiResponse<List<KeywordResponse>> selectKeywords(
            @CurrentUser User user,
            @RequestBody @Valid SelectKeywordsRequest request
    ) {
        return ApiResponse.ok(keywordService.selectKeywords(user, request));
    }
}