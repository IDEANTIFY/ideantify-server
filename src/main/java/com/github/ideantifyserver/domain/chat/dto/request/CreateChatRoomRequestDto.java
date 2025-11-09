package com.github.ideantifyserver.domain.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅방 생성 요청")
public class CreateChatRoomRequestDto {

    @Schema(
            description = "첫 메시지 내용",
            example = "아이디어 검증 플랫폼을 구현하려고해",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}
