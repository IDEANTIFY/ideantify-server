package com.github.ideantifyserver.domain.chat.service;

import com.github.ideantifyserver.domain.chat.dto.AiChatRequestMessage;
import com.github.ideantifyserver.domain.chat.dto.response.*;
import com.github.ideantifyserver.domain.chat.entity.ChatBot;
import com.github.ideantifyserver.domain.chat.entity.ChatRoom;
import com.github.ideantifyserver.domain.chat.exception.ChatExceptionCode;
import com.github.ideantifyserver.domain.chat.repository.ChatBotRepository;
import com.github.ideantifyserver.domain.chat.repository.ChatRoomRepository;
import com.github.ideantifyserver.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatBotRepository chatBotRepository;

    @Autowired(required = false)
    private SqsService sqsService;

    //User 채팅방 생성
    @Transactional
    public CreateChatRoomResponseDto createChatRoom(User user, String content) {
        // 임시 채팅방 생성 (title은 AI 응답 대기)
        ChatRoom chatRoom = ChatRoom.builder()
                .type(ChatRoom.ChatRoomType.USER)
                .user(user)
                .title("대화 생성 중...")
                .build();
        chatRoomRepository.save(chatRoom);

        // 사용자 메시지 저장
        ChatBot userBot = ChatBot.builder()
                .room(chatRoom)
                .role(ChatBot.ChatBotRole.USER)
                .content(content)
                .build();
        chatBotRepository.save(userBot);

        // SQS로 AI에게 요청 전송
        if (sqsService != null) {
            AiChatRequestMessage aiRequest = AiChatRequestMessage.builder()
                    .chatRoomId(chatRoom.getId())
                    .userId(user.getId())
                    .content(content)
                    .type(AiChatRequestMessage.RequestType.CREATE_ROOM)
                    .build();
            sqsService.sendAiChatRequest(aiRequest);
        }

        return CreateChatRoomResponseDto.of(
                chatRoom.getId(),
                chatRoom.getTitle());
    }

    //User 채팅방 조회
    public ChatRoomListResponseDto getChatRooms(User user) {
        List<ChatRoom> roomList = chatRoomRepository.findByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), ChatRoom.ChatRoomType.USER);

        List<ChatRoomResponseDto> chatRoomResponseDtoList = roomList.stream()
                .map(room -> ChatRoomResponseDto.of(
                        room.getId(),
                        room.getTitle(),
                        room.getCreatedAt().toString()
                ))
                .toList();

        return ChatRoomListResponseDto.of(chatRoomResponseDtoList);
    }

    //User에서 메세지 전송
    @Transactional
    public UserChatSendResponseDto sendUserChat(User user, UUID chatRoomId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ChatExceptionCode.CHAT_ROOM_NOT_FOUND::toException);

        if (!chatRoom.getUser().getId().equals(user.getId())) {
            throw ChatExceptionCode.CHAT_ACCESS_DENIED.toException();
        }

        // 사용자 메시지 저장
        ChatBot userMessage = ChatBot.builder()
                .room(chatRoom)
                .role(ChatBot.ChatBotRole.USER)
                .content(content)
                .build();
        chatBotRepository.save(userMessage);

        // SQS로 AI에게 요청 전송
        if (sqsService != null) {
            AiChatRequestMessage aiRequest = AiChatRequestMessage.builder()
                    .chatRoomId(chatRoomId)
                    .userId(user.getId())
                    .content(content)
                    .type(AiChatRequestMessage.RequestType.SEND_MESSAGE)
                    .build();
            sqsService.sendAiChatRequest(aiRequest);
        }

        return UserChatSendResponseDto.of(
                userMessage.getId(),
                content
        );
    }

    //User에서 메세지 조회
    public ChatMessageListResponseDto getUserChatList(User user, UUID chatRoomId, int page, int size) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ChatExceptionCode.CHAT_ROOM_NOT_FOUND::toException);

        if (!chatRoom.getUser().getId().equals(user.getId())) {
            throw ChatExceptionCode.CHAT_ACCESS_DENIED.toException();
        }

        List<ChatMessageDto> messages = chatBotRepository
                .findByRoomIdOrderByCreatedAtAsc(chatRoomId, org.springframework.data.domain.PageRequest.of(page, size))
                .stream()
                .map(ChatMessageDto::from)
                .toList();

        return ChatMessageListResponseDto.of(messages);
    }

}
