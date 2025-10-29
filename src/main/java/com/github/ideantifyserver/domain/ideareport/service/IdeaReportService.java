package com.github.ideantifyserver.domain.ideareport.service;

import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportMetadataRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.response.IdeaReportJobIdResponse;
import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportInput;
import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportTask;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportInputRepository;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportTaskRepository;
import com.github.ideantifyserver.domain.ideareport.sqs.IdeaReportMetadataSqsGateway;
import com.github.ideantifyserver.domain.ideareport.sqs.IdeaReportSqsGateway;
import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import com.github.ideantifyserver.domain.keyword.repository.KeywordRepository;
import com.github.ideantifyserver.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdeaReportService {
    private final IdeaReportSqsGateway gateway;
    private final IdeaReportTaskRepository taskRepository;

    private final IdeaReportInputRepository inputRepository;
    private final KeywordRepository keywordRepository;
    private final IdeaReportMetadataSqsGateway metadataGateway;

    @Value("${app.sqs.idea-report.response-queue}")
    private String ideaReportResponseQueue;

    @Value("${app.sqs.idea-report-metadata.response-queue}")
    private String ideaReportMetadataResponseQueue;

    @Transactional
    public IdeaReportJobIdResponse createAsync(CreateIdeaReportRequestDto request) {
        IdeaReportTask task = IdeaReportTask.builder()
                .query(request.getQuery())
                .status(IdeaReportTask.Status.QUEUED)
                .build();

        taskRepository.save(task);

        gateway.requestReport(task.getId(), request.getQuery(), ideaReportResponseQueue);

        return IdeaReportJobIdResponse.of(task.getId());
    }

    @Transactional
    public UUID createIdeaReportMetadata(CreateIdeaReportMetadataRequestDto req, User user) {
        IdeaReportInput input = IdeaReportInput.builder()
                .query(req.getQuery())
                .summary(req.getSummary())
                .purpose(req.getPurpose())
                .differentiation(req.getDifferentiation())
                .technology(req.getTechnology())
                .target(req.getTarget())
                .user(user)
                .build();
        inputRepository.save(input);

        if (req.getKeywords() != null && !req.getKeywords().isEmpty()) {
            var keywords = keywordRepository.findAllById(req.getKeywords());
            for (Keyword k : keywords) input.addKeyword(k);
        }

        metadataGateway.publish(input.getId(), req, ideaReportMetadataResponseQueue);
        return input.getId();
    }
}
