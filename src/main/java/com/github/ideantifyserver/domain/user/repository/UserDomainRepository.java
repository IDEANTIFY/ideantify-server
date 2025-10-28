package com.github.ideantifyserver.domain.user.repository;

import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.entity.UserDomain;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserDomainRepository extends JpaRepository<UserDomain, UUID> {

    @EntityGraph(attributePaths = {"keyword"})
    List<UserDomain> findByUser(User user);
}
