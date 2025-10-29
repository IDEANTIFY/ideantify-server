package com.github.ideantifyserver.domain.ideareport.repository;

import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportResult;
import com.github.ideantifyserver.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IdeaReportResultRepository extends JpaRepository<IdeaReportResult, UUID> {
    @Query("""
      select r from IdeaReportResult r
      join fetch r.input i
      where i.user = :user
      order by r.createdAt desc
    """)
    List<IdeaReportResult> findAllFetchByUser(@Param("user") User user);
}
