package com.github.ideantifyserver.domain.chat.dto;

import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportResultItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaReportResultItemData {
    private String sourceType;
    private String link;
    private String thumbnail;
    private String summary;
    private String score;
    private String insight;

    public static IdeaReportResultItemData from(IdeaReportResultItem item) {
        return IdeaReportResultItemData.builder()
                .sourceType(item.getSourceType())
                .link(item.getLink())
                .thumbnail(item.getThumbnail())
                .summary(item.getSummary())
                .score(item.getScore())
                .insight(item.getInsight())
                .build();
    }
}
