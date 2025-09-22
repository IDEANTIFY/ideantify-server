package com.github.ideantifyserver.domain.project.repository;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface InnerProjectRepository extends JpaRepository<InnerProject, UUID> {
    List<InnerProject> findDistinctByMembers_User_Id(UUID userId);}
