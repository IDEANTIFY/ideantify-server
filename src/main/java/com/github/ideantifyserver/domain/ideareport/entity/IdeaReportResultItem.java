package com.github.ideantifyserver.domain.ideareport.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IdeaReportResultItem  extends BaseSchema {

    @Column(columnDefinition = "TEXT")
    String sourceType;

    @Column(columnDefinition = "TEXT")
    String title;

    @Column(columnDefinition = "TEXT")
    String keyword;

    @Column(columnDefinition = "TEXT")
    String link;

    @Column(columnDefinition = "TEXT")
    String thumbnail;

    @Column(columnDefinition = "TEXT")
    String summary;

    @Column(columnDefinition = "TEXT")
    String score;

    @Column(columnDefinition = "TEXT")
    String insight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_result_id")
    IdeaReportResult result;

}
