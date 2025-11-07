package com.github.ideantifyserver.domain.keyword.service;

import com.github.ideantifyserver.domain.keyword.dto.request.SelectKeywordsRequest;
import com.github.ideantifyserver.domain.keyword.dto.response.KeywordResponse;
import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import com.github.ideantifyserver.domain.keyword.exception.KeywordExceptions;
import com.github.ideantifyserver.domain.keyword.repository.KeywordRepository;
import com.github.ideantifyserver.domain.user.entity.User;
import com.github.ideantifyserver.domain.user.entity.UserDomain;
import com.github.ideantifyserver.domain.user.repository.UserDomainRepository;
import com.github.ideantifyserver.global.infra.ai.service.AiApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserDomainRepository userDomainRepository;
    private final AiApiService aiApiService;

    @Transactional(readOnly = true)
    public List<KeywordResponse> getAllKeywords() {
        List<Keyword> keywords = keywordRepository.findAll();
        return keywords.stream()
                .map(KeywordResponse::from)
                .toList();
    }

    @Transactional
    public List<KeywordResponse> selectKeywords(User user, SelectKeywordsRequest request) {
        if (request.getKeywordIds() == null || request.getKeywordIds().isEmpty()) {
            throw KeywordExceptions.INVALID_KEYWORD_COUNT.toException();
        }
        
        List<Keyword> keywords = keywordRepository.findAllById(request.getKeywordIds());
        if (keywords.size() != request.getKeywordIds().size()) {
            throw KeywordExceptions.NOT_FOUND.toException();
        }
        
        List<UserDomain> newUserDomains = keywords.stream()
                .map(keyword -> UserDomain.builder()
                        .user(user)
                        .keyword(keyword)
                        .build())
                .toList();

        userDomainRepository.saveAll(newUserDomains);

        // 비동기로 AI API에 유저 정보 전송
        aiApiService.sendUserInfoAsync(user, keywords);

        return keywords.stream()
                .map(KeywordResponse::from)
                .toList();
    }
}
