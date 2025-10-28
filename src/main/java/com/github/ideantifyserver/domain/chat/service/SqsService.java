package com.github.ideantifyserver.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ideantifyserver.domain.chat.dto.AiChatRequestMessage;
import com.github.ideantifyserver.domain.chat.dto.AiChatResponseMessage;
import com.github.ideantifyserver.domain.chat.exception.ChatExceptionCode;
import com.github.ideantifyserver.global.property.SqsProperty;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
public class SqsService {

    private final SqsTemplate sqsTemplate;
    private final SqsProperty sqsProperty;
    private final ObjectMapper objectMapper;
    private final AiResponseProcessor aiResponseProcessor;

    public void sendAiChatRequest(AiChatRequestMessage message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            sqsTemplate.send(to -> to
                    .queue(sqsProperty.getAiRequest())
                    .payload(messageBody)
            );
        } catch (JsonProcessingException e) {
            throw ChatExceptionCode.SQS_MESSAGE_SEND_FAILED.toException();
        } catch (Exception e) {
            throw ChatExceptionCode.SQS_MESSAGE_SEND_FAILED.toException();
        }
    }

    @SqsListener("${spring.cloud.aws.sqs.queue.ai-response}")
    public void receiveAiChatResponse(String messageBody) {
        try {
            AiChatResponseMessage response = objectMapper.readValue(messageBody, AiChatResponseMessage.class);
            aiResponseProcessor.processAiResponse(response);
        } catch (JsonProcessingException e) {
            throw ChatExceptionCode.SQS_MESSAGE_PARSE_FAILED.toException();
        } catch (Exception e) {
            throw ChatExceptionCode.SQS_MESSAGE_PARSE_FAILED.toException();
        }
    }
}
