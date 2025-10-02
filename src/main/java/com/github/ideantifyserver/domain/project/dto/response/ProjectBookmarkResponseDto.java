package com.github.ideantifyserver.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ProjectBookmarkResponseDto {
    boolean bookmarked;
    long bookmarkCount;
}
