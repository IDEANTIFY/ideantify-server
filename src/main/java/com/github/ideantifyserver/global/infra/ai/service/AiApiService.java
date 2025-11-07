package com.github.ideantifyserver.global.infra.ai.service;

import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.global.infra.ai.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiApiService {

    private final RestTemplate restTemplate;

    @Value("${ai.api.url}")
    private String aiApiUrl;

    @Async
    public void sendUserInfoAsync(User user, List<Keyword> keywords) {
        try {
            UserInfoRequest request = UserInfoRequest.builder()
                    .userId(user.getId().toString())
                    .nickname(user.getNickname())
                    .keywords(keywords.stream()
                            .map(Keyword::getName)
                            .toList())
                    .build();

            log.info("Sending user info to AI API: userId={}, nickname={}, keywords={}",
                    request.getUserId(), request.getNickname(), request.getKeywords());

            restTemplate.postForObject(aiApiUrl, request, Void.class);

            log.info("user info 전송 성공");
        } catch (Exception e) {
            log.error("user info 전송 실패", e);
        }
    }
}