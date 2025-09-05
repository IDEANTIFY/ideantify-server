package com.github.ideantifyserver.domain.user.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}),
        indexes = {
                @Index(columnList = "follower_id"),
                @Index(columnList = "following_id")
        }
)
@Entity
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFollow extends BaseSchema {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    User following;
}
