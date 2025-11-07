package com.github.ideantifyserver.domain.ideareport.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateIdeaReportRequestDto {
    @NotBlank
    String query;

    @NotBlank
    String summary;

    @NotBlank
    String purpose;

    @NotBlank
    String differentiation;

    @NotBlank
    String technology;

    @NotBlank
    String target;
}
