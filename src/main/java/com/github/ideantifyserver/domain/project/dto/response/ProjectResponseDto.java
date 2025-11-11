package com.github.ideantifyserver.domain.project.dto.response;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.project.entity.InnerProjectFile;
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
        String github;
        List<UUID> members;
        List<String> files;
        String description;

        public static ProjectResponseDto from(InnerProject project) {

                return ProjectResponseDto.of(
                        project.getId(),
                        project.getCreatedAt(),
                        project.getUpdatedAt(),
                        project.getImage(),
                        project.getSubject(),
                        project.getGithub(),
                        project.getMembers().stream()
                                .map(m -> m.getUser().getId())
                                .toList(),
                        project.getFiles().stream().map(InnerProjectFile::getFile).toList(),
                        project.getDescription()
                );
        }
}
