package com.github.ideantifyserver.domain.ideareport.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IdeaReportInput extends BaseSchema {

    @Column(nullable = false)
    @NotBlank
    String query;

    @Column(nullable = false)
    @NotBlank
    String summary;

    @Column(nullable = false)
    @NotBlank
    String purpose;

    @Column(nullable = false)
    @NotBlank
    String differentiation;

    @Column(nullable = false)
    @NotBlank
    String technology;

    @Column(nullable = false)
    @NotBlank
    String target;

    @OneToMany(mappedBy = "ideaReportInput", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<IdeaReportResult> ideaReportResults = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    Keyword keyword;

}
