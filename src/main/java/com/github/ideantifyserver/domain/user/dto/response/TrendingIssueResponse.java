package com.github.ideantifyserver.domain.user.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrendingIssueResponse(
        String title,
        String link,
        String source,
        LocalDateTime date,
        String image,
        String snippet,
        String matchedKeywords
) {
}
