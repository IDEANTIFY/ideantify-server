package com.github.ideantifyserver.domain.keyword.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectKeywordsRequest {

    @Schema(description = "선택한 키워드 ID 목록", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
    @JsonProperty("keyword_ids")
    @NotEmpty(message = "키워드를 최소 1개 이상 선택해야 합니다.")
    List<UUID> keywordIds;
}