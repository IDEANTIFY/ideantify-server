package com.github.ideantifyserver.domain.ideareport.repository;

import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdeaReportResultRepository extends JpaRepository<IdeaReportResult, UUID> {
}
