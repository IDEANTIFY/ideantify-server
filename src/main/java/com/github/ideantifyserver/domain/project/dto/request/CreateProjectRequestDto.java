package com.github.ideantifyserver.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateProjectRequestDto {
        @NotBlank
        String image;

        @NotBlank
        String subject;

        List<String> keyword;

        String github;

        List<UUID> member;

        List<String> file;

        @NotBlank
        String description;
}
