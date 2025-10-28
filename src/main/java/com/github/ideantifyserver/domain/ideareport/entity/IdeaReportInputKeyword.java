package com.github.ideantifyserver.domain.ideareport.entity;

import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IdeaReportInputKeyword extends BaseSchema {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_input_id", nullable = false)
    IdeaReportInput input;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    Keyword keyword;
}
