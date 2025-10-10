package com.github.ideantifyserver.domain.user.repository;

import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {

    boolean existsByFollowerAndFollowing(User follower, User following);
}
