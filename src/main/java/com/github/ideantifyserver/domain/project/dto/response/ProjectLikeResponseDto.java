package com.github.ideantifyserver.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ProjectLikeResponseDto {
    Boolean liked;
    Long likesCount;
}
