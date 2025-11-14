package com.github.ideantifyserver.domain.user.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocial extends BaseSchema {

    @Column(columnDefinition = "TEXT")
    String github;

    @Column(columnDefinition = "TEXT")
    String linkedin;

    @Column(columnDefinition = "TEXT")
    String instagram;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;

    public void updateSocialLinks(String github, String linkedin, String instagram) {
        this.github = github;
        this.linkedin = linkedin;
        this.instagram = instagram;
    }
}
