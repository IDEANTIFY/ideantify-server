package com.github.ideantifyserver.domain.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 메시지 전송 요청")
public class UserChatRequestDto {

    @Schema(
            description = "메시지 내용",
            example = "이 아이디어는 어때?",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}
