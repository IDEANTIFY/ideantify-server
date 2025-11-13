package com.github.ideantifyserver.domain.chat.controller;

import com.github.ideantifyserver.domain.chat.dto.request.CreateChatRoomRequestDto;
import com.github.ideantifyserver.domain.chat.dto.request.UserChatRequestDto;
import com.github.ideantifyserver.domain.chat.dto.response.*;
import com.github.ideantifyserver.domain.chat.service.ChatService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.resolver.CurrentUser;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "[Chat]")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/users/rooms")
    @Operation(
            summary = "일반 채팅방 생성",
            description = """
                    WebSocket Topic 형식:`/topic/chatRooms/{chatRoomId}`
                    """
    )
    public ApiResponse<CreateChatRoomResponseDto> createChatRoom(
            @CurrentUser(required = false) User user,
            @RequestBody @Valid CreateChatRoomRequestDto request
    ) {
        return ApiResponse.ok(chatService.createChatRoom(user, request.getContent()));
    }

    @PostMapping("/develop/rooms")
    @Operation(
            summary = "아이디어 디벨롭 채팅방 생성",
            description = """
                    WebSocket Topic 형식:`/topic/chatRooms/{chatRoomId}`
                    """
    )
    public ApiResponse<CreateChatRoomResponseDto> createDevelopChatRoom(
            @CurrentUser(required = false) User user,
            @RequestBody @Valid CreateChatRoomRequestDto request
    ) {
        return ApiResponse.ok(chatService.createDevelopChatRoom(user, request.getContent()));
    }

    @PostMapping("/idea-reports/{ideaReportId}/rooms")
    @Operation(
            summary = "아이디어 리포트 채팅방 생성",
            description = """
                    WebSocket Topic 형식:`/topic/chatRooms/{chatRoomId}`
                    """
    )
    public ApiResponse<CreateChatRoomResponseDto> createIdeaReportChatRoom(
            @CurrentUser User user,
            @PathVariable("ideaReportId") UUID ideaReportId,
            @RequestBody @Valid CreateChatRoomRequestDto request
    ) {
        return ApiResponse.ok(chatService.createIdeaReportChatRoom(user, ideaReportId, request.getContent()));
    }


    @GetMapping("/users/rooms")
    @Operation(summary = "채팅방 목록 조회")
    public ApiResponse<ChatRoomListResponseDto> getUserChatRooms(
            @CurrentUser User user
    ) {
        return ApiResponse.ok(chatService.getChatRooms(user));
    }

    @PostMapping("/users/rooms/{chatRoomId}")
    @Operation(
            summary = "메시지 전송",
            description = """
                    WebSocket Topic 형식:`/topic/chatRooms/{chatRoomId}`
                    """
    )
    public ApiResponse<UserChatSendResponseDto> sendUserChat(
            @CurrentUser(required = false) User user,
            @PathVariable("chatRoomId") UUID chatRoomId,
            @RequestBody @Valid UserChatRequestDto request
    ) {
        return ApiResponse.ok(chatService.sendUserChat(user, chatRoomId, request.getContent()));
    }

    @GetMapping("/users/rooms/{chatRoomId}")
    @Operation(summary = "채팅 메시지 목록 조회")
    public ApiResponse<ChatMessageListResponseDto> getUserChatMessages(
            @CurrentUser(required = false) User user,
            @PathVariable("chatRoomId") UUID chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(chatService.getUserChatList(user, chatRoomId, page, size));
    }

}
