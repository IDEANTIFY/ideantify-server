package com.github.ideantifyserver.domain.ideareport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CreateIdeaReportResponseDto {
    String query;
    List<UUID> keyword;
    String summary;
    String purpose;
    String differentiation;
    String technology;
    String target;
}
