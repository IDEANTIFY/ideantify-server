package com.github.ideantifyserver.domain.project.service;

import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import com.github.ideantifyserver.domain.keyword.repository.KeywordRepository;
import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.*;
import com.github.ideantifyserver.domain.project.entity.*;
import com.github.ideantifyserver.domain.project.exception.InnerProjectExceptions;
import com.github.ideantifyserver.domain.project.repository.InnerProjectBookmarkRepository;
import com.github.ideantifyserver.domain.project.repository.InnerProjectRepository;
import com.github.ideantifyserver.domain.project.specification.InnerProjectSpecifications;
import com.github.ideantifyserver.domain.user.dto.response.UserResponseDto;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InnerProjectService {
    private final InnerProjectRepository innerProjectRepository;
    private final InnerProjectBookmarkRepository innerProjectBookmarkRepository;
    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponseDto create(CreateProjectRequestDto req, User me) {
        InnerProject project = InnerProject.builder()
                .image(req.getImage())
                .subject(req.getSubject())
                .github(req.getGithub())
                .description(req.getDescription())
                .build();

        List<String> files = Optional.ofNullable(req.getFiles()).orElseGet(List::of);
        if (files.stream().anyMatch(f -> f == null || f.isBlank())) {
            throw InnerProjectExceptions.INVALID_FILE_PATH.toException();
        }

        files.forEach(f -> project.getFiles().add(InnerProjectFile.builder()
                        .project(project)
                        .file(f)
                        .build())
                );

        if (me == null) throw InnerProjectExceptions.UNAUTHORIZED.toException();

        Set<UUID> memberIds = new HashSet<>(Optional.ofNullable(req.getMembers()).orElseGet(List::of));
        memberIds.add(me.getId());

        List<User> users = memberIds.stream()
                .map(id -> Optional.ofNullable(id)
                        .flatMap(userRepository::findById)
                        .orElseThrow(InnerProjectExceptions.INVALID_MEMBER_ID::toException))
                .toList();

        for (User u : users) {
            project.getMembers().add(
                    InnerProjectMember.builder()
                            .project(project)
                            .user(u)
                            .isOwner(u.getId().equals(me.getId()))
                            .build()
            );
        }

        List<String> names = Optional.ofNullable(req.getKeywords()).orElseGet(List::of).stream()
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

    public List<ProjectListResponseDto> getProjectList(
            boolean bookmarked,
            boolean liked,
            boolean owned,
            Pageable pageable,
            User me
    ) {
        Specification<InnerProject> specification = Specification.allOf();
        if (bookmarked) {
            specification = specification.and(InnerProjectSpecifications.bookmarkedBy(me.getId()));
        }
        if (liked) {
            specification = specification.and(InnerProjectSpecifications.likedBy(me.getId()));
        }
        if (owned) {
            specification = specification.and(InnerProjectSpecifications.memberOf(me.getId()));
        }

        Page<InnerProject> page = innerProjectRepository.findAll(specification, pageable);

        return page.stream()
                .map(p -> ProjectListResponseDto.of(
                        p.getId(),
                        p.getImage(),
                        p.getSubject(),
                        p.getKeywords().stream()
                                .map(InnerProjectKeyword::getKeyword)
                                .map(Keyword::getName)
                                .toList(),
                        p.getMembers().stream()
                                .map(InnerProjectMember::getUser)
                                .map(BaseSchema::getId)
                                .toList()
                ))
                .toList();
    }

    public ProjectDetailResponseDto getProject(UUID id) {
        InnerProject project = innerProjectRepository.findById(id)
                .orElseThrow(InnerProjectExceptions.NOT_FOUND::toException);

        UUID ownerId = project.getMembers().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsOwner()))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElseThrow(InnerProjectExceptions.OWNER_NOT_FOUND::toException);

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
                        .toList(),
                ownerId
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
    public ProjectResponseDto update(UUID id, UpdateProjectRequestDto req, User me) {
        if (me == null) throw InnerProjectExceptions.UNAUTHORIZED.toException();

        InnerProject project = innerProjectRepository.findById(id)
                .orElseThrow(InnerProjectExceptions.NOT_FOUND::toException);

        UUID ownerId = project.getMembers().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsOwner()))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElseThrow(InnerProjectExceptions.OWNER_NOT_FOUND::toException);

        if (!ownerId.equals(me.getId())) {
            throw InnerProjectExceptions.NOT_OWNER.toException();
        }

        project.updateBasics(req.getImage(), req.getSubject(), req.getGithub(), req.getDescription());

        List<String> files = Optional.ofNullable(req.getFiles()).orElseGet(List::of);
        if (files.stream().anyMatch(f -> f == null || f.isBlank())) {
            throw InnerProjectExceptions.INVALID_FILE_PATH.toException();
        }
        List<InnerProjectFile> newFiles = files.stream()
                .map(f -> InnerProjectFile.builder().file(f).build())
                .toList();
        project.updateFiles(newFiles);

        Set<UUID> memberIds = new HashSet<>(Optional.ofNullable(req.getMembers()).orElseGet(List::of));
        memberIds.add(ownerId);

        List<InnerProjectMember> newMembers = memberIds.stream()
                .map(idOpt -> Optional.ofNullable(idOpt)
                        .flatMap(userRepository::findById)
                        .orElseThrow(InnerProjectExceptions.INVALID_MEMBER_ID::toException))
                .map(u -> InnerProjectMember.builder()
                        .user(u)
                        .isOwner(u.getId().equals(ownerId))
                        .build())
                .toList();
        project.updateMembers(newMembers);

        List<String> names = Optional.ofNullable(req.getKeywords()).orElseGet(List::of).stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        if (names.isEmpty() && req.getKeywords() != null && !req.getKeywords().isEmpty()) {
            throw InnerProjectExceptions.INVALID_KEYWORD.toException();
        }

        Map<String, Keyword> keywordMap = ensureKeywords(names);
        List<InnerProjectKeyword> newKeywords = names.stream()
                .map(k -> InnerProjectKeyword.builder()
                        .keyword(keywordMap.get(k))
                        .build())
                .toList();
        project.updateKeywords(newKeywords);

        return ProjectResponseDto.of(
                project.getId(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                project.getImage(),
                project.getSubject(),
                project.getKeywords().stream().map(k -> k.getKeyword().getName()).toList(),
                project.getGithub(),
                project.getMembers().stream().map(m -> m.getUser().getId()).toList(),
                project.getFiles().stream().map(InnerProjectFile::getFile).toList(),
                project.getDescription()
        );
    }

    @Transactional
    public void delete(UUID id, User me) {
        if (me == null) throw InnerProjectExceptions.UNAUTHORIZED.toException();

        InnerProject project = innerProjectRepository.findById(id)
                .orElseThrow(InnerProjectExceptions.NOT_FOUND::toException);

        UUID ownerId = project.getMembers().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsOwner()))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElseThrow(InnerProjectExceptions.OWNER_NOT_FOUND::toException);

        if (!ownerId.equals(me.getId())) {
            throw InnerProjectExceptions.NOT_OWNER.toException();
        }

        innerProjectRepository.delete(project);
    }

    public List<ProjectListResponseDto> getProjectsByUser(UUID userId) {
        List<InnerProject> projects = innerProjectRepository.findAllByMember(userId);

        return projects.stream()
                .map(p -> ProjectListResponseDto.of(
                        p.getId(),
                        p.getImage(),
                        p.getSubject(),
                        p.getKeywords().stream()
                                .map(k -> k.getKeyword().getName())
                                .toList(),
                        p.getMembers().stream()
                                .map(m -> m.getUser().getId())
                                .toList()
                ))
                .toList();
    }

    @Transactional
    public ProjectBookmarkResponseDto bookmarkProject(UUID projectId, User me) {
        if (me == null) throw InnerProjectExceptions.UNAUTHORIZED.toException();

        InnerProject project = innerProjectRepository.findById(projectId)
                .orElseThrow(InnerProjectExceptions.NOT_FOUND::toException);

        if (innerProjectBookmarkRepository.existsByProject_IdAndUser_Id(projectId, me.getId())) {
            throw InnerProjectExceptions.ALREADY_BOOKMARKED.toException();
        }

        try {
            innerProjectBookmarkRepository.save(
                    InnerProjectBookmark.builder()
                            .project(project)
                            .user(me)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            throw InnerProjectExceptions.ALREADY_BOOKMARKED.toException();
        }

        long count = innerProjectBookmarkRepository.countByProject_Id(projectId);

        return ProjectBookmarkResponseDto.of(true, count);
    }

    @Transactional
    public ProjectBookmarkResponseDto unbookmarkProject(UUID projectId, User me) {
        if (me == null) throw InnerProjectExceptions.UNAUTHORIZED.toException();

        innerProjectRepository.findById(projectId)
                .orElseThrow(InnerProjectExceptions.NOT_FOUND::toException);

        if (innerProjectBookmarkRepository.deleteByProject_IdAndUser_Id(projectId, me.getId()) == 0) {
            throw InnerProjectExceptions.NOT_BOOKMARKED.toException();
        }

        long count = innerProjectBookmarkRepository.countByProject_Id(projectId);

        return ProjectBookmarkResponseDto.of(true, count);
    }
}
