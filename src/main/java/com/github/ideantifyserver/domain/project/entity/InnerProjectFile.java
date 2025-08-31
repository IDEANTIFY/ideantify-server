package com.github.ideantifyserver.domain.project.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class InnerProjectFile extends BaseSchema {
    @Column(nullable = false)
    String file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    InnerProject project;
}
