package com.github.ideantifyserver.domain.project.dto.response;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.project.entity.InnerProjectMember;
import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
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
    String description;
    List<UUID> members;

    public static ProjectListResponseDto from(InnerProject project) {

        return ProjectListResponseDto.of(
                project.getId(),
                project.getImage(),
                project.getSubject(),
                project.getDescription(),
                project.getMembers().stream()
                        .map(InnerProjectMember::getUser)
                        .map(BaseSchema::getId)
                        .toList()
        );
    }
}
