package com.github.ideantifyserver.domain.project.repository;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.project.entity.InnerProjectLike;
import com.github.ideantifyserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InnerProjectLikeRepository extends JpaRepository<InnerProjectLike, UUID> {
    boolean existsByProjectAndUser(InnerProject project, User user);
    long countByProject(InnerProject project);
    long deleteByProjectAndUser(InnerProject project, User user);
}
