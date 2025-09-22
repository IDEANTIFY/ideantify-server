package com.github.ideantifyserver.domain.project.repository;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InnerProjectRepository extends JpaRepository<InnerProject, UUID> {
    @Query("""
           select distinct p
           from InnerProject p
           left join p.members m
           where (m.user.id = :userId)
           """)
    List<InnerProject> findAllByMember(UUID userId);
}
