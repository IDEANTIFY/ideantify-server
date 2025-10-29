package com.github.ideantifyserver.domain.ideareport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class IdeaReportListResponseDto {
    UUID id;
    String query;
    String analysisNarrative;
}
