package com.github.ideantifyserver.domain.ideareport.service;

import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.response.IdeaReportJobIdResponse;
import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportTask;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportTaskRepository;
import com.github.ideantifyserver.domain.ideareport.sqs.IdeaReportSqsGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdeaReportService {
    private final IdeaReportSqsGateway gateway;
    private final IdeaReportTaskRepository taskRepository;

    @Value("${app.sqs.response-queue}")
    private String responseQueue;

    @Transactional
    public IdeaReportJobIdResponse createAsync(CreateIdeaReportRequestDto request) {
        IdeaReportTask task = IdeaReportTask.builder()
                .query(request.getQuery())
                .status(IdeaReportTask.Status.QUEUED)
                .build();

        taskRepository.save(task);

        gateway.requestReport(task.getId(), request.getQuery(), responseQueue);

        return IdeaReportJobIdResponse.of(task.getId());
    }
}
