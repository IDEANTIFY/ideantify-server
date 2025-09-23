package com.github.ideantifyserver.domain.user.repository;

import com.github.ideantifyserver.domain.user.entity.UserSocial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSocialRepository extends JpaRepository<UserSocial, UUID> {
}
