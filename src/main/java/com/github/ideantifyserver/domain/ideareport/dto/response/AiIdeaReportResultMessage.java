package com.github.ideantifyserver.domain.ideareport.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiIdeaReportResultMessage {

    SummaryReport summaryReport;
    DetailedReport detailedReport;

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SummaryReport {
        ReportSummary reportSummary;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReportSummary {
        Integer totalSimilarCases;
        EvaluationScoresDto evaluationScores;
        String analysisNarrative;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EvaluationScoresDto {
        Integer similarity;
        Integer creativity;
        Integer feasibility;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetailedReport {
        String query;
        List<ResultItem> detailedResults;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultItem {
        String sourceType;
        String title;
        String keyword;
        String link;
        String thumbnail;
        String summary;
        Double score;
        String insight;
    }
}
