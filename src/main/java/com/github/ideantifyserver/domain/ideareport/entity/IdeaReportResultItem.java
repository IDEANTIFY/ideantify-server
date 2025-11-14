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

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    String sourceType;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    String keyword;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    String link;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    String thumbnail;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    String score;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    String insight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_result_id", nullable = false)
    IdeaReportResult result;

}
