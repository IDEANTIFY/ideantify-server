package com.github.ideantifyserver.domain.project.repository;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InnerProjectRepository extends JpaRepository<InnerProject, UUID>, JpaSpecificationExecutor<InnerProject> {
    @Query("""
           select distinct p
           from InnerProject p
           left join p.members m
           where (m.user = :user)
           """)
    List<InnerProject> findAllByMember(@Param("user") User user);
}
