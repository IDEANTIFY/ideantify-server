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

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdeaReportMetadataResponseListener {
    private final IdeaReportTaskRepository ideaReportTaskRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final @Qualifier("sqsObjectMapper") ObjectMapper sqsObjectMapper;

    private static final String HDR_MESSAGE_TYPE = "messageType";
    private static final String MESSAGE_TYPE     = "IDEA_REPORT";

    @SqsListener("${spring.cloud.aws.sqs.idea-report-metadata.response-queue}")
    public void onResponse(Message<CreateIdeaReportMetadataResponseDto> message) {
        MessageHeaders headers = message.getHeaders();
        String type = String.valueOf(headers.get(HDR_MESSAGE_TYPE));
        String id = String.valueOf(headers.get("jobId"));

        if (!MESSAGE_TYPE.equals(type) || id == null || id.equals("null")) {
            log.debug("무시: type={}, jobId={}", type, id);
            return;
        }

        UUID jobId = UUID.fromString(id);
        CreateIdeaReportMetadataResponseDto payload = message.getPayload();

        try {
            IdeaReportTask task = ideaReportTaskRepository.findById(jobId).orElse(null);
            if (task == null) {
                log.warn("응답 수신했지만 jobId를 못찾음: {}", jobId);
                return;
            }

            task.setStatus(IdeaReportTask.Status.SUCCEEDED);
            task.setResultJson(sqsObjectMapper.writeValueAsString(payload));
            ideaReportTaskRepository.save(task);

            messagingTemplate.convertAndSend("/topic/idea-reports/metadata/" + jobId, payload);
            log.info("웹소켓 푸시 완료: /topic/idea-reports/metadata/{}", jobId);
        } catch (Exception e) {
            log.error("응답 처리 중 오류", e);
            throw new RuntimeException(e);
        }
    }
}
