package com.github.ideantifyserver.domain.project.repository;

import com.github.ideantifyserver.domain.project.entity.InnerProjectComment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InnerProjectCommentRepository extends JpaRepository<InnerProjectComment, UUID> {
    boolean existsByIdAndProject_Id(UUID commentId, UUID projectId);
    Optional<InnerProjectComment> findByIdAndProject_Id(UUID commentId, UUID projectId);
    @EntityGraph(attributePaths = {"user", "parent"})
    List<InnerProjectComment> findAllByProject_IdOrderByCreatedAtAsc(UUID projectId);
}
