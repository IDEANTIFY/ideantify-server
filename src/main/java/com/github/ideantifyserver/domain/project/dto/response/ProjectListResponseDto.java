package com.github.ideantifyserver.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class ProjectListResponseDto {
    UUID id;
    String image;
    String subject;
    List<String> keywords;
    List<UUID> members;
}
