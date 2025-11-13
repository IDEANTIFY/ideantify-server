package com.github.ideantifyserver.domain.ideareport.service;

import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.request.CreateIdeaReportMetadataRequestDto;
import com.github.ideantifyserver.domain.ideareport.dto.response.IdeaReportListResponseDto;
import com.github.ideantifyserver.domain.ideareport.dto.response.IdeaReportResultDetailResponseDto;
import com.github.ideantifyserver.domain.ideareport.entity.*;
import com.github.ideantifyserver.domain.ideareport.exception.IdeaReportExceptions;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportInputRepository;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportResultRepository;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportTaskRepository;
import com.github.ideantifyserver.domain.ideareport.sqs.IdeaReportSqsGateway;
import com.github.ideantifyserver.domain.ideareport.sqs.IdeaReportMetadataSqsGateway;
import com.github.ideantifyserver.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdeaReportService {
    private final IdeaReportMetadataSqsGateway gateway;
    private final IdeaReportTaskRepository taskRepository;

    private final IdeaReportInputRepository inputRepository;
    private final IdeaReportSqsGateway metadataGateway;

    private final IdeaReportResultRepository resultRepository;

    @Value("${spring.cloud.aws.sqs.idea-report.response-queue}")
    private String ideaReportResponseQueue;

    @Value("${spring.cloud.aws.sqs.idea-report-metadata.response-queue}")
    private String ideaReportMetadataResponseQueue;

    @Transactional
    public UUID createMetadata(CreateIdeaReportMetadataRequestDto request) {
        IdeaReportTask task = IdeaReportTask.builder()
                .query(request.getQuery())
                .status(IdeaReportTask.Status.QUEUED)
                .build();

        task = taskRepository.save(task);

        gateway.requestMetadata(task.getId(), request.getQuery(), ideaReportMetadataResponseQueue);

        return task.getId();
    }

    @Transactional
    public UUID createIdeaReport(CreateIdeaReportRequestDto req, User user) {
        IdeaReportInput input = IdeaReportInput.builder()
                .query(req.getQuery())
                .summary(req.getSummary())
                .purpose(req.getPurpose())
                .differentiation(req.getDifferentiation())
                .technology(req.getTechnology())
                .target(req.getTarget())
                .user(user)
                .build();
        input = inputRepository.save(input);

        metadataGateway.publish(input.getId(), req, ideaReportResponseQueue);
        return input.getId();
    }

    public List<IdeaReportListResponseDto> getIdeaReportList(User user) {
        return resultRepository.findAllFetchByUser(user).stream()
                .map(r -> IdeaReportListResponseDto.of(
                        r.getId(),
                        r.getInput().getQuery(),
                        r.getAnalysisNarrative()
                ))
                .toList();
    }

    public IdeaReportResultDetailResponseDto getIdeaReportResult(UUID resultId, User user) {
        IdeaReportResult result = resultRepository.findByIdWithInputAndItems(resultId)
                .orElseThrow(IdeaReportExceptions.NOT_FOUND::toException);

        if (!result.getInput().getUser().getId().equals(user.getId())) {
            throw IdeaReportExceptions.NOT_OWNER.toException();
        }

        EvaluationScores evaluationScores = result.getEvaluationScores();

        return IdeaReportResultDetailResponseDto.of(
                result.getId(),
                result.getInput().getQuery(),
                result.getInput().getSummary(),
                result.getInput().getPurpose(),
                result.getInput().getDifferentiation(),
                result.getInput().getTechnology(),
                result.getInput().getTarget(),
                evaluationScores.getSimilarity(),
                evaluationScores.getCreativity(),
                evaluationScores.getFeasibility(),
                result.getAnalysisNarrative(),
                result.getIdeaReportResultItems().stream()
                        .sorted(
                                Comparator.comparingDouble(
                                        (IdeaReportResultItem e) -> Double.parseDouble(e.getScore())
                                ).reversed()
                        )
                        .map(item -> IdeaReportResultDetailResponseDto.ResultItem.builder()
                                .id(item.getId())
                                .sourceType(item.getSourceType())
                                .title(item.getTitle())
                                .keyword(item.getKeyword())
                                .link(item.getLink())
                                .thumbnail(item.getThumbnail())
                                .summary(item.getSummary())
                                .score(item.getScore())
                                .insight(item.getInsight())
                                .build()
                        )
                        .toList()
        );
    }
}
