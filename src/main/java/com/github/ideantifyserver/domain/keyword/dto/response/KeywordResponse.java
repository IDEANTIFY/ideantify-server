package com.github.ideantifyserver.domain.keyword.dto.response;

import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class KeywordResponse {

    @Schema(description = "키워드 ID")
    UUID id;

    @Schema(description = "키워드 이름")
    String name;

    public static KeywordResponse from(Keyword keyword) {
        return KeywordResponse.of(
                keyword.getId(),
                keyword.getName()
        );
    }
}