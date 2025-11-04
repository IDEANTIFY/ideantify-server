package com.github.ideantifyserver.domain.chat.service;

import com.github.ideantifyserver.domain.chat.dto.AiChatRequestMessage;
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

    //User 채팅방 조회 (모든 타입)
    public ChatRoomListResponseDto getChatRooms(User user) {
        List<ChatRoom> roomList = chatRoomRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<ChatRoomResponseDto> chatRoomResponseDtoList = roomList.stream()
                .map(ChatRoomResponseDto::from)
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

        // 타입별 컨텍스트 생성
        String context = null;
        if (chatRoom.getType() == ChatRoom.ChatRoomType.IDEA_REPORT && chatRoom.getIdeaReport() != null) {
            context = buildIdeaReportContext(chatRoom.getIdeaReport());
        }

        // SQS로 AI에게 요청 전송
        if (sqsService != null) {
            AiChatRequestMessage aiRequest = AiChatRequestMessage.builder()
                    .chatRoomId(chatRoomId)
                    .userId(user.getId())
                    .content(content)
                    .context(context)
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

        // 채팅방 메타데이터 생성
        ChatRoomMetaDto chatRoomMeta = ChatRoomMetaDto.from(chatRoom);

        // 메시지 조회
        List<ChatMessageDto> messages = chatBotRepository
                .findByRoomIdOrderByCreatedAtAsc(chatRoomId, org.springframework.data.domain.PageRequest.of(page, size))
                .stream()
                .map(ChatMessageDto::from)
                .toList();

        return ChatMessageListResponseDto.of(chatRoomMeta, messages);
    }

    // IdeaReport 채팅방 생성
    @Transactional
    public CreateChatRoomResponseDto createIdeaReportChatRoom(User user, UUID ideaReportId, String content) {
        // IdeaReport 조회
        IdeaReportResult ideaReport = ideaReportResultRepository.findById(ideaReportId)
                .orElseThrow(ChatExceptionCode.CHAT_ROOM_NOT_FOUND::toException);

        // 채팅방 생성
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

        // 컨텍스트 생성
        String context = buildIdeaReportContext(ideaReport);

        // SQS로 AI에게 요청 전송
        if (sqsService != null) {
            AiChatRequestMessage aiRequest = AiChatRequestMessage.builder()
                    .chatRoomId(chatRoom.getId())
                    .userId(user.getId())
                    .content(content)
                    .context(context)
                    .type(AiChatRequestMessage.RequestType.CREATE_ROOM)
                    .build();
            sqsService.sendAiChatRequest(aiRequest);
        }

        return CreateChatRoomResponseDto.of(
                chatRoom.getId(),
                chatRoom.getTitle());
    }

    private String buildIdeaReportContext(IdeaReportResult report) {
        StringBuilder context = new StringBuilder();

        context.append("[아이디어 리포트 분석 결과]\n\n");

        // Input 정보
        if (report.getInput() != null) {
            context.append("=== 아이디어 개요 ===\n");
            context.append("검색어: ").append(report.getInput().getQuery()).append("\n");
            context.append("요약: ").append(report.getInput().getSummary()).append("\n");
            context.append("목적: ").append(report.getInput().getPurpose()).append("\n");
            context.append("차별화: ").append(report.getInput().getDifferentiation()).append("\n");
            context.append("기술: ").append(report.getInput().getTechnology()).append("\n");
            context.append("타겟: ").append(report.getInput().getTarget()).append("\n\n");
        }

        // 평가 점수
        if (report.getEvaluationScores() != null) {
            context.append("=== 평가 점수 ===\n");
            context.append("유사성: ").append(report.getEvaluationScores().getSimilarity()).append("점\n");
            context.append("창의성: ").append(report.getEvaluationScores().getCreativity()).append("점\n");
            context.append("실현가능성: ").append(report.getEvaluationScores().getFeasibility()).append("점\n\n");
        }

        // 분석 내러티브
        context.append("=== 분석 ===\n");
        context.append(report.getAnalysisNarrative()).append("\n\n");

        // 유사 사례 수
        context.append("유사 사례 수: ").append(report.getTotalSimilarCases()).append("건\n");

        return context.toString();
    }

}
