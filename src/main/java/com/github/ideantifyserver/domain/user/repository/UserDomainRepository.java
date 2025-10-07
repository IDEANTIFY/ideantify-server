package com.github.ideantifyserver.domain.user.repository;

import com.github.ideantifyserver.domain.user.entity.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserDomainRepository extends JpaRepository<UserDomain, UUID> {
}
