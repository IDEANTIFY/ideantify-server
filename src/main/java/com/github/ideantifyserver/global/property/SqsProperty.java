package com.github.ideantifyserver.global.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.cloud.aws.sqs.queue")
public class SqsProperty {
    // 요청 queue
    private String userChatRequest;        // USER 타입 채팅 요청
    private String developChatRequest;     // DEVELOP 타입 채팅 요청
    private String ideaReportChatRequest;  // IDEA_REPORT 타입 채팅 요청

    // 응답 queue
    private String userChatResponse;        // USER 타입 채팅 응답
    private String developChatResponse;     // DEVELOP 타입 채팅 응답
    private String ideaReportChatResponse;  // IDEA_REPORT 타입 채팅 응답
}
