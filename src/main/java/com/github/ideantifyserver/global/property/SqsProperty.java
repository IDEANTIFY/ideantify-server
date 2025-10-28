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
    private String aiRequest;
    private String aiResponse;
}
