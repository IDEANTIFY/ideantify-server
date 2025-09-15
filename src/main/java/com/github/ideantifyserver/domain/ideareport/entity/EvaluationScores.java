package com.github.ideantifyserver.domain.ideareport.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationScores {

    @Min(0) @Max(100)
    @NotNull
    private Integer similarity;

    @Min(0) @Max(100)
    @NotNull
    private Integer creativity;

    @Min(0) @Max(100)
    @NotNull
    private Integer feasibility;
}
