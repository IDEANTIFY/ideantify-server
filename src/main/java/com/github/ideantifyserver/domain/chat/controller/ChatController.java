package com.github.ideantifyserver.domain.chat.controller;

import com.github.ideantifyserver.domain.chat.dto.request.CreateChatRoomRequestDto;
import com.github.ideantifyserver.domain.chat.dto.request.UserChatRequestDto;
import com.github.ideantifyserver.domain.chat.dto.response.*;
import com.github.ideantifyserver.domain.chat.service.ChatService;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "[Chat]")
public class ChatController {

    private final ChatService chatService;

    // ==================== 채팅방 생성 ====================

    @PostMapping("/api/users/rooms")
    @Operation(summary = "일반 채팅방 생성 (USER)")
    public ApiResponse<CreateChatRoomResponseDto> createChatRoom(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateChatRoomRequestDto request
    ) {
        return ApiResponse.ok(chatService.createChatRoom(user, request.getContent()));
    }

    @PostMapping("/api/develop/rooms")
    @Operation(summary = "아이디어 디벨롭 채팅방 생성 (DEVELOP)")
    public ApiResponse<CreateChatRoomResponseDto> createDevelopChatRoom(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateChatRoomRequestDto request
    ) {
        return ApiResponse.ok(chatService.createDevelopChatRoom(user, request.getContent()));
    }

    @PostMapping("/api/idea-reports/{ideaReportId}/rooms")
    @Operation(summary = "아이디어 리포트 채팅방 생성 (IDEA_REPORT)")
    public ApiResponse<CreateChatRoomResponseDto> createIdeaReportChatRoom(
            @AuthenticationPrincipal User user,
            @PathVariable("ideaReportId") UUID ideaReportId,
            @RequestBody @Valid CreateChatRoomRequestDto request
    ) {
        return ApiResponse.ok(chatService.createIdeaReportChatRoom(user, ideaReportId, request.getContent()));
    }

    // ==================== 채팅방 조회 ====================

    @GetMapping("/api/users/rooms")
    @Operation(summary = "채팅방 목록 조회")
    public ApiResponse<ChatRoomListResponseDto> getUserChatRooms(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(chatService.getChatRooms(user));
    }

    // ==================== 메시지 ====================

    @PostMapping("/api/users/rooms/{chatRoomId}")
    @Operation(summary = "메시지 전송")
    public ApiResponse<UserChatSendResponseDto> sendUserChat(
            @AuthenticationPrincipal User user,
            @PathVariable("chatRoomId") UUID chatRoomId,
            @RequestBody @Valid UserChatRequestDto request
    ) {
        return ApiResponse.ok(chatService.sendUserChat(user, chatRoomId, request.getContent()));
    }

    @GetMapping("/api/users/rooms/{chatRoomId}")
    @Operation(summary = "메시지 조회")
    public ApiResponse<ChatMessageListResponseDto> getUserChatMessages(
            @AuthenticationPrincipal User user,
            @PathVariable("chatRoomId") UUID chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(chatService.getUserChatList(user, chatRoomId, page, size));
    }

}
