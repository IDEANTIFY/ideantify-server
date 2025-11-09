package com.github.ideantifyserver.domain.chat.service;

import com.github.ideantifyserver.domain.chat.dto.AiChatRequestMessage;
import com.github.ideantifyserver.domain.chat.dto.IdeaReportData;
import com.github.ideantifyserver.domain.chat.dto.response.*;
import com.github.ideantifyserver.domain.chat.entity.ChatBot;
import com.github.ideantifyserver.domain.chat.entity.ChatRoom;
import com.github.ideantifyserver.domain.chat.exception.ChatExceptionCode;
import com.github.ideantifyserver.domain.chat.repository.ChatBotRepository;
import com.github.ideantifyserver.domain.chat.repository.ChatRoomRepository;
import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportResult;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportResultRepository;
import com.github.ideantifyserver.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatBotRepository chatBotRepository;
    private final IdeaReportResultRepository ideaReportResultRepository;

    @Autowired(required = false)
    private SqsService sqsService;

    //일반 채팅방 생성
    @Transactional
    public CreateChatRoomResponseDto createChatRoom(User user, String content) {
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

        // SQS로 AI에게 요청
        if (sqsService != null) {
            AiChatRequestMessage aiRequest = AiChatRequestMessage.builder()
                    .chatRoomId(chatRoom.getId())
                    .userId(user.getId())
                    .content(content)
                    .build();
            sqsService.sendAiChatRequest(aiRequest, ChatRoom.ChatRoomType.USER);
        }

        return CreateChatRoomResponseDto.of(
                chatRoom.getId(),
                chatRoom.getTitle());
    }

    // IdeaReport 채팅방 생성
    @Transactional
    public CreateChatRoomResponseDto createIdeaReportChatRoom(User user, UUID ideaReportId, String content) {
        IdeaReportResult ideaReport = ideaReportResultRepository.findById(ideaReportId)
                .orElseThrow(ChatExceptionCode.CHAT_ROOM_NOT_FOUND::toException);

        ChatRoom chatRoom = ChatRoom.builder()
                .type(ChatRoom.ChatRoomType.IDEA_REPORT)
                .user(user)
                .ideaReport(ideaReport)
                .title("아이디어 리포트 분석 중...")
                .build();
        chatRoomRepository.save(chatRoom);

        // 사용자 메시지 저장
        ChatBot userBot = ChatBot.builder()
                .room(chatRoom)
                .role(ChatBot.ChatBotRole.USER)
                .content(content)
                .build();
        chatBotRepository.save(userBot);

        // SQS로 AI에게 요청
        if (sqsService != null) {
            AiChatRequestMessage aiRequest = AiChatRequestMessage.builder()
                    .chatRoomId(chatRoom.getId())
                    .userId(user.getId())
                    .ideaReportId(ideaReportId)
                    .ideaReportData(IdeaReportData.from(ideaReport))
                    .content(content)
                    .build();
            sqsService.sendAiChatRequest(aiRequest, ChatRoom.ChatRoomType.IDEA_REPORT);
        }

        return CreateChatRoomResponseDto.of(
                chatRoom.getId(),
                chatRoom.getTitle());
    }

    // Develop 채팅방 생성
    @Transactional
    public CreateChatRoomResponseDto createDevelopChatRoom(User user, String content) {
        ChatRoom chatRoom = ChatRoom.builder()
                .type(ChatRoom.ChatRoomType.DEVELOP)
                .user(user)
                .title("아이디어 디벨롭 중...")
                .build();
        chatRoomRepository.save(chatRoom);

        // 사용자 메시지 저장
        ChatBot userBot = ChatBot.builder()
                .room(chatRoom)
                .role(ChatBot.ChatBotRole.USER)
                .content(content)
                .build();
        chatBotRepository.save(userBot);

        // SQS로 AI에게 요청
        if (sqsService != null) {
            AiChatRequestMessage aiRequest = AiChatRequestMessage.builder()
                    .chatRoomId(chatRoom.getId())
                    .userId(user.getId())
                    .content(content)
                    .build();
            sqsService.sendAiChatRequest(aiRequest, ChatRoom.ChatRoomType.DEVELOP);
        }

        return CreateChatRoomResponseDto.of(
                chatRoom.getId(),
                chatRoom.getTitle());
    }

    //User 채팅방 조회 (모든 타입)
    public ChatRoomListResponseDto getChatRooms(User user) {
        List<ChatRoom> roomList = chatRoomRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<ChatRoomResponseDto> chatRoomResponseDtoList = roomList.stream()
                .map(ChatRoomResponseDto::from)
                .toList();

        return ChatRoomListResponseDto.of(chatRoomResponseDtoList);
    }

    //메세지 전송 (모든 타입 통합)
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

        // SQS로 AI에게 요청 전송 (타입별로 다른 queue로 전송)
        if (sqsService != null) {
            AiChatRequestMessage.AiChatRequestMessageBuilder builder = AiChatRequestMessage.builder()
                    .chatRoomId(chatRoomId)
                    .userId(user.getId())
                    .content(content);

            // IDEA_REPORT 타입인 경우 ideaReportId와 전체 데이터 포함
            if (chatRoom.getType() == ChatRoom.ChatRoomType.IDEA_REPORT && chatRoom.getIdeaReport() != null) {
                builder.ideaReportId(chatRoom.getIdeaReport().getId());
                builder.ideaReportData(IdeaReportData.from(chatRoom.getIdeaReport()));
            }

            sqsService.sendAiChatRequest(builder.build(), chatRoom.getType());
        }

        return UserChatSendResponseDto.of(
                chatRoomId,
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

        // 채팅방 메타데이터 생성
        ChatRoomMetaDto chatRoomMeta = ChatRoomMetaDto.from(chatRoom);

        // 메시지 조회
        List<ChatMessageDto> messages = chatBotRepository
                .findByRoomIdOrderByCreatedAtAsc(chatRoomId, PageRequest.of(page, size))
                .stream()
                .map(ChatMessageDto::from)
                .toList();

        return ChatMessageListResponseDto.of(chatRoomMeta, messages);
    }

}
