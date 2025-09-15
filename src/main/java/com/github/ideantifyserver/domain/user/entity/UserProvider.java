package com.github.ideantifyserver.domain.user.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "providerId"}),
                @UniqueConstraint(columnNames = {"user_id", "provider"})
        },
        indexes = @Index(columnList = "user_id")
)
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProvider extends BaseSchema {

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    Provider provider;

    @Column(nullable = false)
    @NotBlank
    String providerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    public enum Provider {
        GOOGLE,
        KAKAO
    }
}
