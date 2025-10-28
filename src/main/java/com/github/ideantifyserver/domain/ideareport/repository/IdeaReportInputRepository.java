package com.github.ideantifyserver.domain.ideareport.repository;

import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportInput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdeaReportInputRepository extends JpaRepository<IdeaReportInput, UUID> {
}
