package com.github.ideantifyserver.domain.chat.entity;

import com.github.ideantifyserver.global.infra.mysql.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatBot extends BaseSchema {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotBlank
    ChatBotRole role;

    @Column(nullable = false)
    @NotBlank
    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    ChatRoom room;

    public enum ChatBotRole {
        USER,
        AGENT
    }
}
