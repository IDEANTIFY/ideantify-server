package com.github.ideantifyserver.domain.project.service;

import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import com.github.ideantifyserver.domain.keyword.repository.KeywordRepository;
import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.CommentResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectDetailResponseDto;
import com.github.ideantifyserver.domain.project.dto.response.ProjectResponseDto;
import com.github.ideantifyserver.domain.project.entity.*;
import com.github.ideantifyserver.domain.project.exception.InnerProjectExceptions;
import com.github.ideantifyserver.domain.project.repository.InnerProjectRepository;
import com.github.ideantifyserver.domain.user.dto.response.UserResponseDto;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InnerProjectService {
    private final InnerProjectRepository innerProjectRepository;
    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponseDto create(CreateProjectRequestDto req) {
        InnerProject project = InnerProject.builder()
                .image(req.getImage())
                .subject(req.getSubject())
                .github(req.getGithub())
                .description(req.getDescription())
                .build();

        if (req.getFiles().stream().anyMatch(f -> f == null || f.isBlank())) {
            throw InnerProjectExceptions.INVALID_FILE_PATH.toException();
        }

        Optional.of(req.getFiles()).orElseGet(List::of)
                .forEach(f -> project.getFiles().add(InnerProjectFile.builder()
                        .project(project)
                        .file(f)
                        .build())
                );

        if (req.getMember().stream().anyMatch(Objects::isNull)) {
            throw InnerProjectExceptions.INVALID_MEMBER_ID.toException();
        }

        Optional.of(req.getMember()).orElseGet(List::of)
                .forEach(m -> {
                            User user = userRepository.findById(m).orElseThrow(InnerProjectExceptions.INVALID_MEMBER_ID::toException);
                            project.getMembers().add(InnerProjectMember.builder()
                                    .project(project)
                                    .user(user)
                                    .build());
                });

        List<String> names = Optional.ofNullable(req.getKeyword()).orElseGet(List::of).stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        Map<String, Keyword> keywordMap = ensureKeywords(names);

        names.forEach(n -> project.getKeywords().add(InnerProjectKeyword.builder()
                        .project(project)
                        .keyword(keywordMap.get(n))
                        .build())
        );

        InnerProject saved = innerProjectRepository.save(project);

        return ProjectResponseDto.of(
                saved.getId(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getImage(),
                saved.getSubject(),
                saved.getKeywords().stream()
                        .map(k -> k.getKeyword().getName())
                        .toList(),
                saved.getGithub(),
                saved.getMembers().stream()
                        .map(m -> m.getUser().getId())
                        .toList(),
                saved.getFiles().stream().map(InnerProjectFile::getFile).toList(),
                saved.getDescription()
        );
    }

    private Map<String, Keyword> ensureKeywords(List<String> names) {
        if (names.isEmpty()) return Map.of();

        Map<String, Keyword> byName = keywordRepository.findByNameIn(names).stream()
                .collect(Collectors.toMap(Keyword::getName, k -> k, (a, b)->a));

        List<Keyword> toCreate = names.stream()
                .filter(n -> !byName.containsKey(n))
                .map(n -> Keyword.builder().name(n).build())
                .toList();

        if (!toCreate.isEmpty()) {
            keywordRepository.saveAll(toCreate).forEach(k -> byName.put(k.getName(), k));
        }
        return byName;
    }

    public ProjectDetailResponseDto getProject(UUID id) {
        InnerProject project = innerProjectRepository.findById(id)
                .orElseThrow(InnerProjectExceptions.NOT_FOUND::toException);

        return ProjectDetailResponseDto.of(
                project.getId(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                project.getImage(),
                project.getSubject(),
                project.getKeywords().stream()
                        .map(k -> k.getKeyword().getName())
                        .toList(),
                project.getGithub(),
                project.getMembers().stream()
                        .map(m -> m.getUser().getId())
                        .toList(),
                project.getFiles().stream()
                        .map(InnerProjectFile::getFile)
                        .toList(),
                project.getDescription(),
                project.getComments().stream()
                        .map(this::toCommentDto)
                        .toList()
        );
    }

    private CommentResponseDto toCommentDto(InnerProjectComment comment) {
        return CommentResponseDto.of(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                UserResponseDto.of(
                        comment.getUser().getId(),
                        comment.getUser().getNickname(),
                        comment.getUser().getAvatar()
                ),
                comment.getContent(),
                comment.getChildren().stream()
                        .map(this::toCommentDto)
                        .toList()
        );
    }

    @Transactional
    public void delete(UUID id) {
        InnerProject project = innerProjectRepository.findById(id)
                .orElseThrow(InnerProjectExceptions.NOT_FOUND::toException);

        innerProjectRepository.delete(project);
    }
}
