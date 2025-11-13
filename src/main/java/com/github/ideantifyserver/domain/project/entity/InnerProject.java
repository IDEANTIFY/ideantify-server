package com.github.ideantifyserver.domain.project.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class InnerProject extends BaseSchema {

    @Column(nullable = false)
    @NotBlank
    String image;

    @Column(nullable = false)
    @NotBlank
    String subject;

    @Column
    String github;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<InnerProjectFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<InnerProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<InnerProjectBookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<InnerProjectLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<InnerProjectComment> comments = new ArrayList<>();

    public void updateBasics(String image, String subject, String github, String description) {
        this.image = image;
        this.subject = subject;
        this.github = github;
        this.description = description;
    }

    public void updateFiles(List<InnerProjectFile> newFiles) {
        this.files.clear();
        for (InnerProjectFile f : newFiles) {
            f.setProject(this);
            this.files.add(f);
        }
    }

    public void updateMembers(List<InnerProjectMember> newMembers) {
        this.members.clear();
        for (InnerProjectMember m : newMembers) {
            m.setProject(this);
            this.members.add(m);
        }
    }
}
