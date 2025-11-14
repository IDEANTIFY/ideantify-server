package com.github.ideantifyserver.domain.ideareport.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ideantifyserver.domain.ideareport.dto.response.CreateIdeaReportMetadataResponseDto;
import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportTask;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportTaskRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdeaReportMetadataResponseListener {
    private final IdeaReportTaskRepository ideaReportTaskRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final @Qualifier("sqsObjectMapper") ObjectMapper sqsObjectMapper;

    @SqsListener("${spring.cloud.aws.sqs.idea-report-metadata.response-queue}")
    public void onResponse(Message<CreateIdeaReportMetadataResponseDto> message) {

        try {
            log.info("[SQS] 아이디어 리포트 메타데이터 응답 수신");

            MessageHeaders headers = message.getHeaders();

            software.amazon.awssdk.services.sqs.model.Message sqsMessage =
                    (software.amazon.awssdk.services.sqs.model.Message) headers.get("Sqs_SourceData");

            Map<String, MessageAttributeValue> attributes = Objects.requireNonNull(sqsMessage).messageAttributes();

            UUID id = UUID.fromString(attributes.get("id").stringValue());

            CreateIdeaReportMetadataResponseDto payload = message.getPayload();

            IdeaReportTask task = ideaReportTaskRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("아이디어 리포트 작업을 찾을 수 없습니다: " + id));

            task.setStatus(IdeaReportTask.Status.SUCCEEDED);
            task.setResultJson(sqsObjectMapper.writeValueAsString(payload));
            ideaReportTaskRepository.save(task);

            messagingTemplate.convertAndSend("/topic/idea-reports/metadata/" + id, payload);
            log.info("웹소켓 푸시 완료: /topic/idea-reports/metadata/{}", id);
        } catch (Throwable e) {
            log.error("응답 처리 중 오류", e);
        }
    }
}
