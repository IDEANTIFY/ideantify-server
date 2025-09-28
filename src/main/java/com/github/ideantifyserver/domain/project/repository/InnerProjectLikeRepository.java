package com.github.ideantifyserver.domain.project.repository;

import com.github.ideantifyserver.domain.project.entity.InnerProjectLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InnerProjectLikeRepository extends JpaRepository<InnerProjectLike, UUID> {
    boolean existsByProject_IdAndUser_Id(UUID projectId, UUID userId);
    Long countByProject_Id(UUID projectId);
    void deleteByProject_IdAndUser_Id(UUID projectId, UUID userId);
}
