package com.github.ideantifyserver.domain.ideareport.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdeaReportMetadataSqsGateway {
    private final SqsTemplate sqsTemplate;

    @Value("${spring.cloud.aws.sqs.idea-report-metadata.request-queue}")
    private String requestQueue;

    private static final String HDR_MESSAGE_TYPE = "messageType";
    private static final String MESSAGE_TYPE     = "IDEA_REPORT_METADATA";

    public void requestMetadata(UUID jobId, String query, String responseQueue) {
        IdeaReportMetadataRequestMessage payload = new IdeaReportMetadataRequestMessage(jobId, query, responseQueue);

        sqsTemplate.send(t -> t
                .queue(requestQueue)
                .payload(payload)
                .header(HDR_MESSAGE_TYPE, MESSAGE_TYPE)
                .header("jobId", jobId.toString())
        );
    }

    @Getter
    @AllArgsConstructor
    public static class IdeaReportMetadataRequestMessage {
        private UUID jobId;
        private String query;
        private String replyTo;
    }
}
