package com.github.ideantifyserver.domain.ideareport.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    String site;

    @Column(nullable = false)
    @NotBlank
    String source;

    @Column(nullable = false)
    @NotBlank
    String summary;

    @Column(nullable = false)
    @Min(0) @Max(100)
    int similarity;

    @Column(nullable = false)
    @Min(0) @Max(100)
    int creativity;

    @Column(nullable = false)
    @Min(0) @Max(100)
    int feasibility;

    @Column
    String uploader;

    @Column
    String uploaderImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_result_id", nullable = false)
    IdeaReportResult result;

}
