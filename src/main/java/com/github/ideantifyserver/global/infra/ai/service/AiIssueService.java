package com.github.ideantifyserver.global.infra.ai.service;

import com.github.ideantifyserver.domain.user.dto.response.TrendingIssueResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiIssueService {

    private final RestTemplate restTemplate;

    @Value("${ai.api.url}")
    private String aiApiUrl;

    /**
     * AI 서버에 비동기로 뉴스 검색 요청
     */
    @Async
    public CompletableFuture<List<TrendingIssueResponse>> fetchTrendingIssuesAsync(List<String> keywords) {
        try {
            String targetUrl = aiApiUrl + "/search/news";
            Map<String, Object> requestBody = Map.of("keywords", keywords);

            log.info("[AI API 요청 시작] -> {}", targetUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<AiResponse> response =
                    restTemplate.exchange(targetUrl, HttpMethod.POST, entity, AiResponse.class);

            AiResponse body = response.getBody();
            if (body == null || body.getResults() == null) {
                log.warn("AI 응답이 비어 있음");
                return CompletableFuture.completedFuture(List.of());
            }

            // --- JSON 결과를 record에 매핑 ---
            List<TrendingIssueResponse> results = body.getResults().stream()
                    .map(r -> TrendingIssueResponse.builder()
                            .title(r.get("title"))
                            .link(r.get("link"))
                            .source(r.get("source"))
                            .date(parseDate(r.get("date")))
                            .image(r.get("image"))
                            .snippet(r.get("snippet"))
                            .matchedKeywords(r.get("matched_keywords"))
                            .build())
                    .toList();

            log.info("[AI 응답 성공] count={}", results.size());
            return CompletableFuture.completedFuture(results);

        } catch (Exception e) {
            log.error("[AI API 요청 실패]", e);
            return CompletableFuture.completedFuture(List.of());
        }
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            return LocalDateTime.parse(dateStr); // ISO-8601 형식인 경우 ("2025-11-11T12:34:56")
        } catch (DateTimeParseException e) {
            // 예: "2025-11-11" 같은 단순 날짜인 경우
            try {
                return LocalDateTime.parse(dateStr + "T00:00:00");
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    @Data
    public static class AiResponse {
        private int count;
        private List<Map<String, String>> results;
    }
}
