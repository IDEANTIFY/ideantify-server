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
        ReportQuery query;
        List<ResultItem> detailedResults;
        Object rawReport;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReportQuery {
        String differentiation;
        String purpose;
        String query;
        String summary;
        String target;
        String technology;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultItem {
        String sourceType;
        String summary;
        String title;
        String image;
        Double score;
        String insight;
    }
}
