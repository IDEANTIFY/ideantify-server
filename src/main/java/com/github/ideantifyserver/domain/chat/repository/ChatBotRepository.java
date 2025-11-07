package com.github.ideantifyserver.domain.chat.repository;

import com.github.ideantifyserver.domain.chat.entity.ChatBot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatBotRepository extends JpaRepository<ChatBot, UUID> {
    Page<ChatBot> findByRoomIdOrderByCreatedAtAsc(UUID roomId, Pageable pageable);
}
