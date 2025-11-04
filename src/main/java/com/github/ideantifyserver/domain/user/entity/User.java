package com.github.ideantifyserver.domain.user.entity;

import com.github.ideantifyserver.domain.project.entity.InnerProjectMember;
import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseSchema implements UserContext {

    @Override
    public String getUserId() {

        return this.getId().toString();
    }

    @Column(nullable = false, unique = true)
    @NotBlank
    String nickname;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Email
    String email;

    @Column
    String avatar;

    @OneToMany(mappedBy = "user", cascade =  CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    @Builder.Default
    List<UserProvider> providers = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<UserFollow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<UserFollow> followings = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    UserSocial social;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<InnerProjectMember> projects = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<UserDomain> keywords = new ArrayList<>();

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateAvatar(String avatar) {
        this.avatar = avatar;
    }
}
