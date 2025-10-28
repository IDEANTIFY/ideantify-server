package com.github.ideantifyserver.domain.project.repository;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.project.entity.InnerProjectComment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InnerProjectCommentRepository extends JpaRepository<InnerProjectComment, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select c
        from InnerProjectComment c
        where c.id = :commentId and c.project = :project
    """)
    Optional<InnerProjectComment> findForUpdateByIdAndProject(UUID commentId, InnerProject project);

    @EntityGraph(attributePaths = {"user", "parent"})
    List<InnerProjectComment> findAllByProjectAndDeletedFalseOrderByCreatedAtAsc(InnerProject project);
}
