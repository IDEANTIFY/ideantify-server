package com.github.ideantifyserver.domain.ideareport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class IdeaReportResultDetailResponseDto {
    UUID id;
    String query;
    String summary;
    String purpose;
    String differentiation;
    String technology;
    String target;
    Integer similarity;
    Integer creativity;
    Integer feasibility;
    String analysisNarrative;
    List<ResultItem> detailedResults;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResultItem {
        UUID id;
        String sourceType;
        String title;
        String keyword;
        String link;
        String thumbnail;
        String summary;
        String score;
        String insight;
    }
}
