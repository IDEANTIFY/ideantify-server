package com.github.ideantifyserver.global.infra.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequest {

    private String userId;
    private String nickname;
    private List<String> keywords;
}