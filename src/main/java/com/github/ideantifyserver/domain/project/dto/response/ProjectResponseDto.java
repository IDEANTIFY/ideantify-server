package com.github.ideantifyserver.domain.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class ProjectResponseDto{
        UUID id;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
        String image;
        String subject;
        List<String> keywords;
        String github;
        List<UUID> members;
        List<String> files;
        String description;
}
