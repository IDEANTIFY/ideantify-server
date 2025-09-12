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

    @Column(nullable = false)
    @NotBlank
    String sourceType;

    @Column(nullable = false)
    @NotBlank
    String link;

    @Column(nullable = false)
    @NotBlank
    String thumbnail;

    @Column(nullable = false)
    @NotBlank
    String summary;

    @Column(nullable = false)
    @NotBlank
    String score;

    @Column(nullable = false)
    @NotBlank
    String insight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_result_id", nullable = false)
    IdeaReportResult result;

}
