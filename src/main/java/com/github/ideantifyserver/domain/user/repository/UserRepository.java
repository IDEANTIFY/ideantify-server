package com.github.ideantifyserver.domain.user.repository;

import com.github.ideantifyserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"providers", "followers", "followings", "keywords"})
    Optional<User> findByEmail(String email);
}
