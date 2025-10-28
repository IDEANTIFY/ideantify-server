package com.github.ideantifyserver.domain.chat.controller;

import com.github.ideantifyserver.domain.chat.dto.request.CreateChatRoomRequestDto;
import com.github.ideantifyserver.domain.chat.dto.request.UserChatRequestDto;
import com.github.ideantifyserver.domain.chat.dto.response.*;
import com.github.ideantifyserver.domain.chat.service.ChatService;
import com.github.ideantifyserver.global.response.ApiResponse;
import com.github.ideantifyserver.global.security.oauth.CustomOAuth2UserDetails;
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

    @PostMapping("/api/users/rooms")
    @Operation(summary = "채팅방 생성")
    public ApiResponse<CreateChatRoomResponseDto> createChatRoom(
            @AuthenticationPrincipal CustomOAuth2UserDetails user,
            @RequestBody @Valid CreateChatRoomRequestDto request
    ) {
        return ApiResponse.ok(chatService.createChatRoom(user.getUser(), request.getContent()));
    }

    @GetMapping("/api/users/rooms")
    @Operation(summary = "채팅 목록 조회")
    public ApiResponse<ChatRoomListResponseDto> getUserChatRooms(
            @AuthenticationPrincipal CustomOAuth2UserDetails user
            ){
        return ApiResponse.ok(chatService.getChatRooms(user.getUser()));
    }

    @PostMapping("/api/users/rooms/{chatRoomId}")
    @Operation(summary = "유저 채팅 메세지 전송")
    public ApiResponse<UserChatSendResponseDto> SendUserChat(
            @AuthenticationPrincipal CustomOAuth2UserDetails user,
            @PathVariable("chatRoomId") UUID chatRoomId,
            @RequestBody @Valid UserChatRequestDto request
    ){
        return ApiResponse.ok(chatService.sendUserChat(user.getUser(), chatRoomId, request.getContent()));
    }

    @GetMapping("/api/users/rooms/{chatRoomId}")
    @Operation(summary = "유저 채팅 메세지 조회")
    public ApiResponse<ChatMessageListResponseDto> getUserChatMessages(
            @AuthenticationPrincipal CustomOAuth2UserDetails user,
            @PathVariable("chatRoomId") UUID chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return ApiResponse.ok(chatService.getUserChatList(user.getUser(), chatRoomId, page, size));
    }

}
