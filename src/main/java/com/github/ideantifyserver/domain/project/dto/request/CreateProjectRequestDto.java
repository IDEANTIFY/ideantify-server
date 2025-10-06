package com.github.ideantifyserver.domain.project.dto.request;

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
public class CreateProjectRequestDto implements UpsertProjectRequestDto {

        @NotBlank
        String image;

        @NotBlank
        String subject;

        List<String> keywords;

        String github;

        List<UUID> members;

        List<String> files;

        @NotBlank
        String description;
}
