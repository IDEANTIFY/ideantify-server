package com.github.ideantifyserver.domain.user.repository;

import com.github.ideantifyserver.domain.user.entity.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query("SELECT distinct u FROM User u WHERE :query IS NULL or u.nickname LIKE CONCAT('%', :query, '%') or u.email LIKE CONCAT('%', :query, '%')")
    List<User> findByNicknameOrEmail(@Param("query") String query, Limit limit);
}
