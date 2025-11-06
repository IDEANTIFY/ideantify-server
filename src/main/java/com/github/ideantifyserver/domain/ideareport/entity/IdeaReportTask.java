package com.github.ideantifyserver.domain.ideareport.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IdeaReportTask extends BaseSchema {

    @Column(nullable = false)
    String query;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Lob
    private String resultJson;

    private String errorMessage;

    public enum Status { QUEUED, RUNNING, SUCCEEDED, FAILED }
}
