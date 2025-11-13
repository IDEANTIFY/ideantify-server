package com.github.ideantifyserver.domain.project.service;

import com.github.ideantifyserver.domain.project.dto.request.CreateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.request.UpdateProjectRequestDto;
import com.github.ideantifyserver.domain.project.dto.response.*;
import com.github.ideantifyserver.domain.project.entity.*;
import com.github.ideantifyserver.domain.project.exception.InnerProjectExceptions;
import com.github.ideantifyserver.domain.project.repository.InnerProjectCommentRepository;
import com.github.ideantifyserver.domain.project.repository.InnerProjectBookmarkRepository;
import com.github.ideantifyserver.domain.project.repository.InnerProjectLikeRepository;
import com.github.ideantifyserver.domain.project.repository.InnerProjectRepository;
import com.github.ideantifyserver.domain.project.specification.InnerProjectSpecifications;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import com.github.ideantifyserver.global.exception.GlobalExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InnerProjectService {
    private final InnerProjectRepository innerProjectRepository;
    private final InnerProjectCommentRepository innerProjectCommentRepository;
    private final InnerProjectBookmarkRepository innerProjectBookmarkRepository;
    private final InnerProjectLikeRepository innerProjectLikeRepository;
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
            throw GlobalExceptions.INVALID_REQUEST.toException();
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
                        .orElseThrow(GlobalExceptions.INVALID_REQUEST::toException))
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

        InnerProject saved = innerProjectRepository.save(project);
        return ProjectResponseDto.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectListResponseDto> getProjectList(
            boolean bookmarked,
            boolean liked,
            boolean owned,
            UUID userId,
            Pageable pageable,
            User me
    ) {
        boolean needUser = bookmarked || liked || owned;

        User targetUser = null;
        if (needUser) {
            if (userId != null) {
                targetUser = userRepository.findById(userId).orElseThrow(GlobalExceptions.INVALID_REQUEST::toException);
            } else {
                targetUser = me;
            }
        }

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

        List<InnerProjectComment> all = innerProjectCommentRepository
                .findAllByProjectAndDeletedFalseOrderByCreatedAtAsc(project);

        Map<UUID, List<InnerProjectComment>> childrenMap = all.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId(), LinkedHashMap::new, Collectors.toList()));

        List<InnerProjectComment> roots = all.stream()
                .filter(c -> c.getParent() == null)
                .toList();

        List<CommentResponseDto> comments = roots.stream()
                .map(c -> CommentResponseDto.from(c, childrenMap))
                .toList();

        UUID ownerId = project.getMembers().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsOwner()))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElseThrow(GlobalExceptions.NOT_FOUND::toException);

        return ProjectDetailResponseDto.from(project, comments, ownerId);
    }

    @Transactional
    public ProjectResponseDto update(InnerProject project, UpdateProjectRequestDto req, User me) {
        UUID ownerId = project.getMembers().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsOwner()))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElseThrow(GlobalExceptions.NOT_FOUND::toException);

        if (!ownerId.equals(me.getId())) {
            throw GlobalExceptions.NOT_PERMITTED.toException();
        }

        project.updateBasics(req.getImage(), req.getSubject(), req.getGithub(), req.getDescription());

        List<String> files = Optional.ofNullable(req.getFiles()).orElseGet(List::of);
        if (files.stream().anyMatch(f -> f == null || f.isBlank())) {
            throw GlobalExceptions.INVALID_REQUEST.toException();
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
                        .orElseThrow(GlobalExceptions.INVALID_REQUEST::toException))
                .map(u -> InnerProjectMember.builder()
                        .user(u)
                        .isOwner(u.getId().equals(ownerId))
                        .build())
                .toList();
        project.updateMembers(newMembers);

        return ProjectResponseDto.from(project);
    }

    @Transactional
    public void delete(InnerProject project, User me) {
        UUID ownerId = project.getMembers().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsOwner()))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElseThrow(GlobalExceptions.NOT_FOUND::toException);

        if (!ownerId.equals(me.getId())) {
            throw GlobalExceptions.NOT_PERMITTED.toException();
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

    @Transactional
    public CreatedCommentResponseDto addComment(InnerProject project, UUID parentId, String content, User me) {
        InnerProjectComment parent = Optional.ofNullable(parentId)
                .map(id -> innerProjectCommentRepository
                        .findForUpdateByIdAndProject(id, project)
                        .orElseThrow(GlobalExceptions.NOT_FOUND::toException))
                .orElse(null);

        InnerProjectComment comment = InnerProjectComment.builder()
                .content(content)
                .parent(parent)
                .user(me)
                .project(project)
                .build();

        innerProjectCommentRepository.saveAndFlush(comment);

        return CreatedCommentResponseDto.from(comment);
    }

    @Transactional
    @PreAuthorize("#project == #comment.project and #comment.user == #me")
    public CreatedCommentResponseDto updateComment(InnerProject project, InnerProjectComment comment, String content, User me) {
        comment.updateContent(content);
        return CreatedCommentResponseDto.from(comment);
    }

    @Transactional
    @PreAuthorize("#project == #comment.project and #comment.user == #me")
    public void deleteComment(InnerProject project, InnerProjectComment comment, User me) {
        if (comment.isDeleted()) return;
        comment.markDeleted();
    }
}
