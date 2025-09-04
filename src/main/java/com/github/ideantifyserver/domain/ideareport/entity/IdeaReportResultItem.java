package com.github.ideantifyserver.domain.ideareport.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
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
    @NotBlank
    int similarity;

    @Column(nullable = false)
    @NotBlank
    int creativiity;

    @Column(nullable = false)
    @NotBlank
    int feasibility;

    @Column
    String uploader;

    @Column
    String uploaderImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_result_id", nullable = false)
    IdeaReportResult result;

}
