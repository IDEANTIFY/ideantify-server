package com.github.ideantifyserver.domain.ideareport.entity;

import com.github.ideantifyserver.domain.chat.entity.ChatRoom;
import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IdeaReportResult extends BaseSchema {

    @Column(nullable = false)
    @NotBlank
    String evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_input_id", nullable = false)
    IdeaReportInput input;

    @OneToMany(mappedBy = "ideaReportResult", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<IdeaReportResultItem> ideaReportResultItems = new ArrayList<>();

    @OneToMany(mappedBy = "ideaReportResult", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<ChatRoom> chatRooms = new ArrayList<>();
}
