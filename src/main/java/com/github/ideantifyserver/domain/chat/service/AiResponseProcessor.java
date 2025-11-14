package com.github.ideantifyserver.domain.chat.service;

import com.github.ideantifyserver.domain.chat.dto.AiChatResponseMessage;
import com.github.ideantifyserver.domain.chat.dto.response.ChatMessageWebSocketDto;
import com.github.ideantifyserver.domain.chat.entity.ChatBot;
import com.github.ideantifyserver.domain.chat.entity.ChatRoom;
import com.github.ideantifyserver.domain.chat.exception.ChatExceptionCode;
import com.github.ideantifyserver.domain.chat.repository.ChatBotRepository;
import com.github.ideantifyserver.domain.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
public class AiResponseProcessor {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatBotRepository chatBotRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void processAiResponse(AiChatResponseMessage response) {
        ChatRoom chatRoom = chatRoomRepository.findById(response.getChatRoomId())
                .orElseThrow(ChatExceptionCode.CHAT_ROOM_NOT_FOUND::toException);

        // title 업데이트 (채팅방 생성 시)
        if (response.getTitle() != null && !response.getTitle().isEmpty()) {
            chatRoom.updateTitle(response.getTitle());
        }

        // AI 메시지 저장
        ChatBot agentMessage = ChatBot.builder()
                .room(chatRoom)
                .role(ChatBot.ChatBotRole.AGENT)
                .content(response.getContent())
                .build();
        chatBotRepository.save(agentMessage);

        // WebSocket으로 실시간 푸시
        ChatMessageWebSocketDto webSocketMessage = ChatMessageWebSocketDto.builder()
                .chatRoomId(response.getChatRoomId())
                .role("AGENT")
                .content(response.getContent())
                .timestamp(LocalDateTime.now().toString())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/chatRooms/" + response.getChatRoomId(),
                webSocketMessage
        );
    }
}
