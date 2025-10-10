package com.github.ideantifyserver.domain.ideareport.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdeaReportSqsGateway {
    private final SqsTemplate sqsTemplate;

    @Value("${app.sqs.request-queue}")
    private String requestQueue;

    private static final String HDR_MESSAGE_TYPE = "messageType";
    private static final String MESSAGE_TYPE     = "IDEA_REPORT";

    public void requestReport(UUID jobId, String query, String responseQueue) {
        IdeaReportRequestMessage payload = new IdeaReportRequestMessage(jobId, query, responseQueue);

        sqsTemplate.send(t -> t
                .queue(requestQueue)
                .payload(payload)
                .header(HDR_MESSAGE_TYPE, MESSAGE_TYPE)
                .header("jobId", jobId.toString())
        );
    }

    @Getter
    @AllArgsConstructor
    public static class IdeaReportRequestMessage {
        private UUID jobId;
        private String query;
        private String replyTo; // 사용 안 할 수도 있지만 계약상 포함
    }
}
