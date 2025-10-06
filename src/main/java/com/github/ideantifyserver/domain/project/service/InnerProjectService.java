package com.github.ideantifyserver.domain.project.service;

import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import com.github.ideantifyserver.domain.keyword.repository.KeywordRepository;
import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.*;
import com.github.ideantifyserver.domain.project.entity.*;
import com.github.ideantifyserver.domain.project.exception.InnerProjectExceptions;
import com.github.ideantifyserver.domain.project.repository.InnerProjectBookmarkRepository;
import com.github.ideantifyserver.domain.project.repository.InnerProjectLikeRepository;
import com.github.ideantifyserver.domain.project.repository.InnerProjectRepository;
import com.github.ideantifyserver.domain.project.specification.InnerProjectSpecifications;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
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
public class InnerProjectService {
    private final InnerProjectRepository innerProjectRepository;
    private final InnerProjectBookmarkRepository innerProjectBookmarkRepository;
    private final InnerProjectLikeRepository innerProjectLikeRepository;
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
        return ProjectResponseDto.from(saved);
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

    @Transactional(readOnly = true)
    public List<ProjectListResponseDto> getProjectList(
            boolean bookmarked,
            boolean liked,
            boolean owned,
            User user,
            Pageable pageable,
            User me
    ) {
        User targetUser = user != null ? user : me;

        Specification<InnerProject> specification = Specification.allOf();
        if (bookmarked) {
            specification = specification.and(InnerProjectSpecifications.bookmarkedBy(targetUser));
        }
        if (liked) {
            specification = specification.and(InnerProjectSpecifications.likedBy(targetUser));
        }
        if (owned) {
            specification = specification.and(InnerProjectSpecifications.memberOf(targetUser));
        }

        Page<InnerProject> page = innerProjectRepository.findAll(specification, pageable);

        return page.stream()
                .map(ProjectListResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponseDto getProject(InnerProject project) {

        UUID ownerId = project.getMembers().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsOwner()))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElseThrow(InnerProjectExceptions.OWNER_NOT_FOUND::toException);

        return ProjectDetailResponseDto.from(project, ownerId);
    }

    @Transactional
    public ProjectResponseDto update(InnerProject project, UpdateProjectRequestDto req, User me) {
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

        return ProjectResponseDto.from(project);
    }

    @Transactional
    public void delete(InnerProject project, User me) {
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

    @Transactional
    public ProjectBookmarkResponseDto bookmarkProject(InnerProject project, User me) {
        if (innerProjectBookmarkRepository.existsByProjectAndUser(project, me)) {
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

        long count = innerProjectBookmarkRepository.countByProject(project);

        return ProjectBookmarkResponseDto.of(true, count);
    }

    @Transactional
    public ProjectBookmarkResponseDto unbookmarkProject(InnerProject project, User me) {
        if (innerProjectBookmarkRepository.deleteByProjectAndUser(project, me) == 0) {
            throw InnerProjectExceptions.NOT_BOOKMARKED.toException();
        }

        long count = innerProjectBookmarkRepository.countByProject(project);

        return ProjectBookmarkResponseDto.of(false, count);
    }

    @Transactional
    public ProjectLikeResponseDto likeProject(InnerProject project, User me) {
        if (innerProjectLikeRepository.existsByProjectAndUser(project, me)) {
            throw InnerProjectExceptions.ALREADY_LIKED.toException();
        }

        try {
            innerProjectLikeRepository.save(
                    InnerProjectLike.builder()
                            .project(project)
                            .user(me)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            throw InnerProjectExceptions.ALREADY_LIKED.toException();
        }

        long count = innerProjectLikeRepository.countByProject(project);
        return ProjectLikeResponseDto.of(true, count);
    }

    @Transactional
    public ProjectLikeResponseDto unlikeProject(InnerProject project, User me) {
        if (innerProjectLikeRepository.deleteByProjectAndUser(project, me) == 0) {
            throw InnerProjectExceptions.NOT_LIKED.toException();
        }

        long count = innerProjectLikeRepository.countByProject(project);

        return ProjectLikeResponseDto.of(false, count);
    }
}
