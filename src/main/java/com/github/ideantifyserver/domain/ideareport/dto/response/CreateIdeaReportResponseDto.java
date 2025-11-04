package com.github.ideantifyserver.domain.ideareport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CreateIdeaReportResponseDto {
    String query;
    String summary;
    String purpose;
    String differentiation;
    String technology;
    String target;
}
