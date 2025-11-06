package com.github.ideantifyserver.domain.chat.entity;

import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportResult;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseSchema {

    @Column
    String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ChatRoomType type;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<ChatBot> chatBots = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_report_result_id")
    IdeaReportResult ideaReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    public enum ChatRoomType {
        USER,
        DEVELOP,
        IDEA_REPORT
    }
}
