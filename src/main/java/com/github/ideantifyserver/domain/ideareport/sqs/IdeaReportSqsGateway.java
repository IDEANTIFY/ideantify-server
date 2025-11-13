package com.github.ideantifyserver.domain.ideareport.sqs;

import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportRequestDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdeaReportSqsGateway {

    private final SqsTemplate sqsTemplate;

    @Value("${spring.cloud.aws.sqs.idea-report.request-queue}")
    private String requestQueue;

    private static final String HDR_MESSAGE_TYPE = "messageType";
    private static final String MESSAGE_TYPE     = "IDEA_REPORT_RESULT";

    public void publish(UUID inputId, CreateIdeaReportRequestDto dto, String responseQueue) {
        sqsTemplate.send(to -> to
                .queue(requestQueue)
                .payload(dto)
                .header(HDR_MESSAGE_TYPE, MESSAGE_TYPE)
                .header("jobId", inputId.toString())
                .header("responseQueue", responseQueue)
        );
    }
}
