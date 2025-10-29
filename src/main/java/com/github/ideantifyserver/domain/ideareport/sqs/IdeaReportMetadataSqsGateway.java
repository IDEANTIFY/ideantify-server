package com.github.ideantifyserver.domain.ideareport.sqs;

import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportMetadataRequestDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdeaReportMetadataSqsGateway {

    private final SqsTemplate sqsTemplate;

    @Value("${app.sqs.idea-report-metadata.request-queue}")
    private String requestQueue;

    private static final String HDR_TYPE = "messageType";
    private static final String HDR_INPUT_ID = "inputId";

    public void publish(UUID inputId, CreateIdeaReportMetadataRequestDto dto, String responseQueue) {
        sqsTemplate.send(to -> to
                .queue(requestQueue)
                .payload(dto)
                .header(HDR_TYPE, "IDEA_REPORT_REQUEST")
                .header(HDR_INPUT_ID, inputId.toString())
                .header("responseQueue", responseQueue)
        );
    }
}
