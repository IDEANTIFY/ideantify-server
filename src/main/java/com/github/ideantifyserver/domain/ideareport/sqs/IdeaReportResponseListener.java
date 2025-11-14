package com.github.ideantifyserver.domain.ideareport.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ideantifyserver.domain.ideareport.dto.response.AiIdeaReportResultMessage;
import com.github.ideantifyserver.domain.ideareport.dto.response.IdeaReportResponseDto;
import com.github.ideantifyserver.domain.ideareport.entity.EvaluationScores;
import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportInput;
import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportResult;
import com.github.ideantifyserver.domain.ideareport.entity.IdeaReportResultItem;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportInputRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdeaReportResponseListener {

    private final IdeaReportInputRepository inputRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final @Qualifier("sqsObjectMapper") ObjectMapper sqsObjectMapper;

    @Transactional
    @SqsListener("https://sqs.ap-northeast-2.amazonaws.com/749000350951/ideantify-idea-report-response.fifo")
    public void onResponse(Message<String> message) {
        log.info("[SQS] 아이디어 리포트 응답 수신");

        MessageHeaders headers = message.getHeaders();

        software.amazon.awssdk.services.sqs.model.Message sqsMessage =
                (software.amazon.awssdk.services.sqs.model.Message) headers.get("Sqs_SourceData");

        Map<String, MessageAttributeValue> attributes = Objects.requireNonNull(sqsMessage).messageAttributes();

        UUID id = UUID.fromString(attributes.get("id").stringValue());

        try {
            AiIdeaReportResultMessage resultMessage = sqsObjectMapper.readValue(message.getPayload(), AiIdeaReportResultMessage.class);
            AiIdeaReportResultMessage.ReportSummary summary = resultMessage.getSummaryReport().getReportSummary();
            AiIdeaReportResultMessage.EvaluationScoresDto scores = summary.getEvaluationScores();

            IdeaReportInput ideaReportInput = inputRepository.findById(id).orElse(null);
            if (ideaReportInput == null) {
                log.warn("응답 수신했지만 id를 못찾음: {}", id);
                return;
            }

            IdeaReportResult result = IdeaReportResult.builder()
                    .evaluationScores(
                            new EvaluationScores(
                                    scores.getSimilarity(),
                                    scores.getCreativity(),
                                    scores.getFeasibility()
                            )
                    )
                    .totalSimilarCases(summary.getTotalSimilarCases())
                    .analysisNarrative(summary.getAnalysisNarrative())
                    .build();

            resultMessage.getDetailedReport().getDetailedResults().forEach(resultItem -> {
                IdeaReportResultItem item = IdeaReportResultItem.builder()
                        .result(result)
                        .sourceType(resultItem.getSourceType() == null ? "-" : resultItem.getSourceType())
                        .title(resultItem.getTitle() == null ? "-" : resultItem.getTitle())
                        .keyword(resultItem.getKeyword() == null ? "" : resultItem.getKeyword())
                        .link(resultItem.getLink() == null ? "-" : resultItem.getLink())
                        .thumbnail(resultItem.getThumbnail() == null ? "-" : resultItem.getThumbnail())
                        .summary(resultItem.getSummary() == null ? "-" : resultItem.getSummary())
                        .score(resultItem.getScore() == null ? "0" : String.valueOf(resultItem.getScore()))
                        .insight(resultItem.getInsight() == null ? "-" : resultItem.getInsight())
                        .build();
                result.getIdeaReportResultItems().add(item);
            });

            ideaReportInput.setResult(result);
            IdeaReportInput saved = inputRepository.save(ideaReportInput);

            IdeaReportResponseDto responseDto =
                    IdeaReportResponseDto.of(
                            saved.getId(),
                            scores.getSimilarity(),
                            scores.getCreativity(),
                            scores.getFeasibility(),
                            summary.getAnalysisNarrative(),
                            saved.getResult().getIdeaReportResultItems().stream()
                                    .sorted(
                                            Comparator.comparingDouble(
                                                    (IdeaReportResultItem e) -> Double.parseDouble(e.getScore())
                                            ).reversed()
                                    )
                                    .map(item -> IdeaReportResponseDto.ResultItem.builder()
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
                                    ).toList()
                    );

            messagingTemplate.convertAndSend("/topic/idea-reports/" + id, responseDto);
            log.info("웹소켓 푸시 완료: /topic/idea-report/{}", id);
        } catch (Exception e) {
            log.error("AI 응답 처리 실패", e);
            throw new RuntimeException(e);
        }
    }
}
