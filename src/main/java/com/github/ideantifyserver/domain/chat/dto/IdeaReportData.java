package com.github.ideantifyserver.domain.chat.dto;

import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaReportData {
    // Input 정보
    private String query;
    private String summary;
    private String purpose;
    private String differentiation;
    private String technology;
    private String target;

    // Evaluation Scores
    private Integer similarity;
    private Integer creativity;
    private Integer feasibility;

    // Result 정보
    private Integer totalSimilarCases;
    private String analysisNarrative;

    // Result Items
    private List<IdeaReportResultItemData> resultItems;

    public static IdeaReportData from(IdeaReportResult ideaReport) {
        return IdeaReportData.builder()
                // Input 정보
                .query(ideaReport.getInput().getQuery())
                .summary(ideaReport.getInput().getSummary())
                .purpose(ideaReport.getInput().getPurpose())
                .differentiation(ideaReport.getInput().getDifferentiation())
                .technology(ideaReport.getInput().getTechnology())
                .target(ideaReport.getInput().getTarget())
                // Evaluation Scores
                .similarity(ideaReport.getEvaluationScores().getSimilarity())
                .creativity(ideaReport.getEvaluationScores().getCreativity())
                .feasibility(ideaReport.getEvaluationScores().getFeasibility())
                // Result 정보
                .totalSimilarCases(ideaReport.getTotalSimilarCases())
                .analysisNarrative(ideaReport.getAnalysisNarrative())
                // Result Items
                .resultItems(
                        ideaReport.getIdeaReportResultItems().stream()
                                .map(IdeaReportResultItemData::from)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
