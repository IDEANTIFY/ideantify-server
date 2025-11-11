package com.github.ideantifyserver.domain.project.dto.request;

import java.util.List;
import java.util.UUID;

public interface UpsertProjectRequestDto {

    String getImage();

    String getSubject();

    String getGithub();

    List<UUID> getMembers();

    List<String> getFiles();

    String getDescription();
}
