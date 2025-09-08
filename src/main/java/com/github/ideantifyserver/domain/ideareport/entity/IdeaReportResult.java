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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IdeaReportResult extends BaseSchema {

    @Column(nullable = false)
    @NotBlank
    String evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_input_id", nullable = false)
    IdeaReportInput input;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<IdeaReportResultItem> ideaReportResultItems = new ArrayList<>();

    @OneToMany(mappedBy = "ideaReport", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<ChatRoom> chatRooms = new ArrayList<>();
}
