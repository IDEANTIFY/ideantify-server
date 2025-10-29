package com.github.ideantifyserver.domain.ideareport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class IdeaReportMetadataResponseDto {
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
        String sourceType;
        String title;
        String link;
        String thumbnail;
        String summary;
        Double score;
        String insight;
    }
}
