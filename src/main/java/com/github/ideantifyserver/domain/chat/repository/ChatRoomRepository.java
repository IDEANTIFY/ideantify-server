package com.github.ideantifyserver.domain.chat.repository;

import com.github.ideantifyserver.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    List<ChatRoom> findByUserIdAndTypeOrderByCreatedAtDesc(UUID id, ChatRoom.ChatRoomType chatRoomType);
}
