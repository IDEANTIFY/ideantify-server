package com.github.ideantifyserver.domain.ideareport.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateIdeaReportMetadataRequestDto {
    @NotBlank
    String query;

    List<UUID> keywords;

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
