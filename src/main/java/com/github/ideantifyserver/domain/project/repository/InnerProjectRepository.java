package com.github.ideantifyserver.domain.project.repository;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InnerProjectRepository extends JpaRepository<InnerProject, UUID> {
}
