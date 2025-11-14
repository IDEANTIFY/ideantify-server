package com.github.ideantifyserver.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ideantifyserver.domain.chat.dto.AiChatRequestMessage;
import com.github.ideantifyserver.domain.chat.dto.AiChatResponseMessage;
import com.github.ideantifyserver.domain.chat.entity.ChatRoom;
import com.github.ideantifyserver.domain.chat.exception.ChatExceptionCode;
import com.github.ideantifyserver.global.property.SqsProperty;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
public class SqsService {

    private final SqsTemplate sqsTemplate;
    private final SqsProperty sqsProperty;
    private final @Qualifier("sqsObjectMapper") ObjectMapper sqsObjectMapper;
    private final AiResponseProcessor aiResponseProcessor;

    public void sendAiChatRequest(AiChatRequestMessage message, ChatRoom.ChatRoomType type) {
        try {
            String messageBody = sqsObjectMapper.writeValueAsString(message);

            // 타입별로 다른 queue로 전송
            String queueUrl = switch (type) {
                case USER -> sqsProperty.getUserChatRequest();
                case DEVELOP -> sqsProperty.getDevelopChatRequest();
                case IDEA_REPORT -> sqsProperty.getIdeaReportChatRequest();
            };

            sqsTemplate.send(to -> to
                    .queue(queueUrl)
                    .payload(messageBody)
                    .header("chatRoomId", message.getChatRoomId().toString())
            );
        } catch (JsonProcessingException e) {
            throw ChatExceptionCode.SQS_MESSAGE_SEND_FAILED.toException();
        } catch (Exception e) {
            throw ChatExceptionCode.SQS_MESSAGE_SEND_FAILED.toException();
        }
    }

    @SqsListener("${spring.cloud.aws.sqs.queue.user-chat-response}")
    public void receiveUserChatResponse(Message<String> message) {
        try {
            String headerChatRoomId = (String) message.getHeaders().get("chatRoomId");

            String messageBody = message.getPayload();
            log.info("SQS_MESSAGE 수신 [USER_CHAT]: headers={}, body={}", message.getHeaders(), messageBody);

            AiChatResponseMessage response = sqsObjectMapper.readValue(messageBody, AiChatResponseMessage.class);

            if (headerChatRoomId != null && !headerChatRoomId.equals(response.getChatRoomId().toString())) {
                throw ChatExceptionCode.CHAT_ROOM_ID_MISMATCH.toException();
            }

            aiResponseProcessor.processAiResponse(response);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패 [USER_CHAT]: {}", e.getMessage(), e);
            throw ChatExceptionCode.SQS_MESSAGE_PARSE_FAILED.toException();
        } catch (Exception e) {
            log.error("메시지 처리 실패 [USER_CHAT]: {}", e.getMessage(), e);
            throw ChatExceptionCode.SQS_MESSAGE_PARSE_FAILED.toException();
        }
    }

    @SqsListener("${spring.cloud.aws.sqs.queue.develop-chat-response}")
    public void receiveDevelopChatResponse(Message<String> message) {
        try {
            String headerChatRoomId = (String) message.getHeaders().get("chatRoomId");

            String messageBody = message.getPayload();
            log.info("SQS_MESSAGE 수신 [DEVELOP_CHAT]: headers={}, body={}", message.getHeaders(), messageBody);

            AiChatResponseMessage response = sqsObjectMapper.readValue(messageBody, AiChatResponseMessage.class);

            if (headerChatRoomId != null && !headerChatRoomId.equals(response.getChatRoomId().toString())) {
                throw ChatExceptionCode.CHAT_ROOM_ID_MISMATCH.toException();
            }

            aiResponseProcessor.processAiResponse(response);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패 [DEVELOP_CHAT]: {}", e.getMessage(), e);
            throw ChatExceptionCode.SQS_MESSAGE_PARSE_FAILED.toException();
        } catch (Exception e) {
            log.error("메시지 처리 실패 [DEVELOP_CHAT]: {}", e.getMessage(), e);
            throw ChatExceptionCode.SQS_MESSAGE_PARSE_FAILED.toException();
        }
    }

    @SqsListener("${spring.cloud.aws.sqs.queue.idea-report-chat-response}")
    public void receiveIdeaReportChatResponse(Message<String> message) {
        try {
            String headerChatRoomId = (String) message.getHeaders().get("chatRoomId");

            String messageBody = message.getPayload();
            log.info("SQS_MESSAGE 수신 [IDEA_REPORT_CHAT]: headers={}, body={}", message.getHeaders(), messageBody);

            AiChatResponseMessage response = sqsObjectMapper.readValue(messageBody, AiChatResponseMessage.class);

            if (headerChatRoomId != null && !headerChatRoomId.equals(response.getChatRoomId().toString())) {
                throw ChatExceptionCode.CHAT_ROOM_ID_MISMATCH.toException();
            }

            aiResponseProcessor.processAiResponse(response);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패 [IDEA_REPORT_CHAT]: {}", e.getMessage(), e);
            throw ChatExceptionCode.SQS_MESSAGE_PARSE_FAILED.toException();
        } catch (Exception e) {
            log.error("메시지 처리 실패 [IDEA_REPORT_CHAT]: {}", e.getMessage(), e);
            throw ChatExceptionCode.SQS_MESSAGE_PARSE_FAILED.toException();
        }
    }
}
