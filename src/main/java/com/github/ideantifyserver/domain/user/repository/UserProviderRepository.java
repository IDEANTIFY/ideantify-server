package com.github.ideantifyserver.domain.user.repository;

import com.github.ideantifyserver.domain.user.entity.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProviderRepository extends JpaRepository<UserProvider, UUID> {
}
