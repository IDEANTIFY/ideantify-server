package com.github.ideantifyserver.domain.keyword.entity;

import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportInput;
import com.github.ideantifyserver.domain.user.entity.UserDomain;
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
public class Keyword extends BaseSchema {

    @Column(nullable = false)
    @NotBlank
    String name;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<UserDomain> userDomains = new ArrayList<>();

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<IdeaReportInput> ideaReportInputs = new ArrayList<>();


}
