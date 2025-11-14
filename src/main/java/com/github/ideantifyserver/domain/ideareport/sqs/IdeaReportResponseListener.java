package com.github.ideantifyserver.domain.ideareport.sqs;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ideantifyserver.domain.ideareport.dto.response.AiIdeaReportResultMessage;
import com.github.ideantifyserver.domain.ideareport.dto.response.IdeaReportResponseDto;
import com.github.ideantifyserver.domain.ideareport.entity.*;
import com.github.ideantifyserver.domain.ideareport.repository.IdeaReportInputRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
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

    @SqsListener("https://sqs.ap-northeast-2.amazonaws.com/749000350951/ideantify-idea-report-response.fifo")
    public void onResponse(Message<String> message) {
        log.info("[SQS] 아이디어 리포트 응답 수신: {}", message.toString());

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
                        .sourceType(resultItem.getSourceType())
                        .title(resultItem.getTitle())
                        .thumbnail(resultItem.getImage())
                        .summary(resultItem.getSummary())
                        .score(String.valueOf(resultItem.getScore()))
                        .insight(resultItem.getInsight())
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

//    @PostConstruct
//    public void test() {
//
//        final String MOCK_DATA = "{\"summary_report\": {\"report_summary\": {\"total_similar_cases\": 9, \"evaluation_scores\": {\"similarity\": 68, \"creativity\": 60, \"feasibility\": 70}, \"analysis_narrative\": \"본 아이디어는 강의 시간표와 팀원 일정의 교차 분석으로 최적 회의 시간을 제안한다는 점에서 대학생 팀 협업 도구와 차별화됩니다. 다만 웹의 일반 시장 자료는 기능 개념의 중복만 제시할 뿐, 교육 현장의 데이터 구조와 정책 이슈를 반영하지 못합니다. 크롤링 DB의 SaaS 관찰은 기술적 가능성을 시사하지만, 학사 일정의 복잡성과 개인정보 보호 이슈를 고려해야 합니다. 차별화 포인트는 다중 레이어 분석(강의·실습·세미나 일정 vs 팀 가용성)과 강의 스케줄과 팀 일정 간 충돌 최소화를 자동 제안하는 기능입니다. 구현은 스케줄링 알고리즘과 데이터 파이프라인으로 가능하나 Campus IT 인프라 연동(SSO, 학번/소속, 권한 관리)과 데이터 품질 확보가 선행 과제이며, 파일럿은 특정 학과나 팀으로 시작해 점진적으로 확장하는 것이 바람직합니다.\"}}, \"detailed_report\": {\"query\": {\"query\": \"강의 시간표와 팀원 일정표를 분석해 최적 회의 시간을 추천해주는 서비스\", \"summary\": \"시간표-일정 분석 기반의 회의 시간 최적화 서비스\", \"purpose\": \"회의 시간 최적화\", \"differentiation\": \"강의-팀원 일정 교차 분석 기반 차별화\", \"technology\": \"스케줄링 알고리즘, 데이터 분석 파이프라인\", \"target\": \"교육기관 및 팀/조직\"}, \"detailed_results\": [{\"source_type\": \"web\", \"title\": \"성공창업을 위한 시장조사 및 시장분석\", \"summary\": \"시장 환경과 규모, 성장률을 파악하고 경쟁 구도와 자사 경쟁력을 평가해 향후 전략을 수립하는 시장조사·시장분석 방법을 제시하는 자료입니다.\", \"score\": 0.7726229429244995, \"insight\": \"<기존 아이디어> 시간표 기반 회의 최적화의 시장성 평가와 경쟁 현황 파악이 핵심 요소로 간주됩니다.\\n<내 아이디어> 교육기관 및 팀/조직 대상의 회의 시간 최적화 서비스로서, 시장 규모 추정과 경쟁 구도 분석을 기반으로 차별화 포지션을 설계해야 합니다. 구독형 SaaS 모델이나 교육기관 라이선스 모델 등을 고려해 수익 흐름을 명확히 설정하고, 고객 세그먼트에 따른 가치 제안을 구체화하는 것이 좋겠습니다.\\n(Keyword: N/A, Team Members: N/A, Link: https://repository.kisti.re.kr/bitstream/10580/6312/1/2016-008%20%EC%84%B1%EA%B3%B5%EC%B0%BD%EC%97%85%EC%9D%84%20%EC%9C%84%ED%95%9C%20%EC%8B%9C%EC%9E%A5%EC%A1%B0%EC%82%AC%20%EB%B0%8F%20%EC%8B%9C%EC%9E%A5%EB%B6%84%EC%84%9D.pdf, Source: N/A)\", \"image\": \"\"}, {\"source_type\": \"web\", \"title\": \"경쟁업체를 분석 노하우 - 300cbt | 삼백씨비티\", \"summary\": \"경쟁 분석의 방법론과 절차, 도구를 제시하고 실행 주기까지 구체화하여 경쟁 우위를 확보하는 노하우를 다룬 자료입니다.\", \"score\": 0.7656759023666382, \"insight\": \"<기존 아이디어> 경쟁사 분석의 기본 프레임과 모니터링 주기가 제시되어 있습니다.\\n<내 아이디어> 회의 시간 최적화 서비스에서 경쟁자를 식별할 때 캘린더/일정 관리 도구, 교육용 협업 플랫폼 등을 후보로 삼고, Cross-기능 차별화 포인트를 도출해야 합니다. 예를 들어 timetables와 팀 일정의 교차 분석 가능 여부, 프라이버시 설정, 교육 기관 특화 기능 등을 검토해 차별화 전략을 구체화하십시오.\\n(Keyword: N/A, Team Members: N/A, Link: https://300cbt.com/blogs/ecommerce-marketing/what-is-a-competitive-analysis-how-to-guide?srsltid=AfmBOorwiUYKiLFkavrwxEvhPeH8dWDWLnufX1FeDH63BlZLihGSG6gA, Source: N/A)\", \"image\": \"\"}, {\"source_type\": \"web\", \"title\": \"시장 경쟁자 분석 : 경쟁 우위를 확보합니다\", \"summary\": \"경쟁사 분석의 중요성과 시장 동향, 고객 선호도 파악을 통한 전략 수립의 필요성을 강조하는 자료입니다.\", \"score\": 0.7615815997123718, \"insight\": \"<기존 아이디어> 경쟁 우위 확보를 위한 경쟁사 분석의 중요성을 확인합니다.\\n<내 아이디어> 회의 시간 최적화 서비스를 계획할 때, 교육기관 및 팀 단위의 특성을 반영한 경쟁사 비교표를 작성하고, 데이터 분석 파이프라인을 활용한 차별화 지점(예: 교차 일정 분석, 프라이버시 보안 관리)을 구체화하십시오.\\n(Keyword: N/A, Team Members: N/A, Link: https://www.verifiedmarketresearch.com/ko/blog/understanding-market-competitors-a-comprehensive-analysis-in-market-research/, Source: N/A)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"지속적인 기술 모니터링 및 분석을 통해 기업의 기술 위치 변화와 외부 요인을 신속히 파악하고, 전략적으로 대응할 수 있도록 지원하는 솔루션\", \"summary\": \"클라우드/SaaS 기술 모니터링 및 분석에 관한 외부 사례를 소개하는 자료입니다.\", \"score\": 0.6819478273391724, \"insight\": \"<기존 아이디어> SaaS 기반 회의 최적화 서비스의 기술 위치와 외부 요인 모니터링 필요성에 공감합니다.\\n<내 아이디어> 서비스 운영 측면에서 지속적인 모니터링 및 성능 지표 추적, 업데이트 주기 정의, 벤치마크 관리를 도입해 안정성과 경쟁력을 높이십시오. (Keyword: 클라우드/SaaS, Team Members: 넥스트솔, Link: https://www.data.go.kr/suc/awardWinning.do, Source: 외부)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"스마트 팜 IoT 기반 유기농 작물 재배 솔루션\", \"summary\": \"IoT 센서와 AI를 결합한 스마트팜 시스템으로 실시간 모니터링과 자동 제어를 통해 작물 생산성을 높이는 사례입니다.\", \"score\": 0.40555164217948914, \"insight\": \"<기존 아이디어> 현 아이디어와 직접 연관성은 낮지만 AI/데이터 분석의 응용 가능성을 보여주는 사례입니다.\\n<내 아이디어> 회의 시간 최적화 알고리즘에서도 센서형 데이터(참석 가능 여부, 방문자 가용성 등)와 예측 모델을 도입해 더 정교한 추천을 시도할 수 있습니다. 다만 도메인 차이를 명확히 하고, 개인정보 이슈를 고려해야 합니다.\\n(Keyword: IoT, 스마트팜, 유기농, AI, 농업, Team Members: 류성민, 배진호, 신혜원, Link: /project/user_004, Source: Iideantify 유저)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"AR 기반 가상 화장실 데모 및 쇼핑 서비스\", \"summary\": \"AR 기술을 활용해 공간에 가구 등을 배치해보는 데모 및 쇼핑 서비스 사례입니다.\", \"score\": 0.3760179579257965, \"insight\": \"<기존 아이디어> AR/가상 시각화 기술의 구현 사례를 보여 줍니다.\\n<내 아이디어> 회의 시간 추천 시스템에 AR을 적용하기보다는 UI/UX에서의 직관성 향상이나, 특정 공간(예: 캠퍼스 내 회의실)의 시각화된 일정 배치를 통해 사용 경험을 강화하는 방향으로 참고할 수 있습니다. (Keyword: AR, 가상 데모, 인테리어, 쇼핑, Team Members: 박수빈, 최민석, 김나연, Link: /project/user_008, Source: Iideantify 유저)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"실시간 식품 안전성 검증 블록체인 플랫폼\", \"summary\": \"블록체인 기술로 생산자-소비자 간의 식품 이력 및 안전 정보를 추적하는 플랫폼 사례입니다.\", \"score\": 0.3491707444190979, \"insight\": \"<기존 아이디어> 데이터 투명성과 신뢰성 구축의 중요성을 시사합니다.\\n<내 아이디어> 회의 일정 데이터의 무결성과 접근 제어를 강화하는 보안 모델을 고려해 신뢰성 있는 협업 플랫폼으로의 확장을 모색해 보십시오. (Keyword: 식품 안전, 블록체인, 공급망 추적, 투명성, Team Members: 정수진, 최동욱, Link: /project/user_002, Source: Iideantify 유저)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"대학생을 위한 팀플 일정 관리 및 협업 도구\", \"summary\": \"대학생 팀 프로젝트를 위한 일정 관리 및 협업 도구로, 캘린더 연동을 통해 팀원 일정 분석 및 최적의 회의 시간을 제안하는 기능을 제공합니다.\", \"score\": 0.3402193784713745, \"insight\": \"<기존 아이디어> 대학생 대상의 팀 프로젝트 협업 도구와 일정 최적화의 구체적 사례를 보여 줍니다.\\n<내 아이디어> 본 아이디어의 핵심과 가장 유사한 사례로, 캘린더 데이터 기반 회의 시간 제안 및 팀 단위 최적화에 대한 실전적 벤치마크를 제공합니다. 이를 바탕으로 교육기관용 확장, 프라이버시 옵션 강화, 과제 트래킹 연계 등 확장 방향을 모색하십시오. (Keyword: 팀플, 일정 관리, 협업, 대학생, Team Members: 강미영, 윤태현, 한지우, 오서연, Link: /project/user_003, Source: Iideantify 유저)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"AI 기반 개인 맞춤형 영양 관리 플랫폼\", \"summary\": \"사용자의 건강 상태와 생활 습관을 분석해 AI가 맞춤형 영양 계획을 제공하는 모바일 애플리케이션 사례입니다.\", \"score\": 0.32036375999450684, \"insight\": \"<기존 아이디어> AI 기반 개인화 추천 시스템의 구현 가능성을 확인합니다.\\n<내 아이디어> 일정 추천 알고리즘에 개인화 요소를 도입하되, 팀 구성원별 선호도나 작업 우선순위에 따른 맞춤형 제안으로 확장할 수 있습니다. 다만 도메인 특성상 개인정보 보호와 데이터 최소 수집 원칙을 엄격히 적용해야 합니다. (Keyword: AI, 영양 관리, 건강 관리, 모바일 앱, Team Members: 김철수, 이영희, 박민수, Link: /project/user_001, Source: Iideantify 유저)\", \"image\": \"\"}], \"raw_report\": {\"detailed_results\": [{\"source_type\": \"web\", \"title\": \"성공창업을 위한 시장조사 및 시장분석\", \"summary\": \"시장 환경과 규모, 성장률을 파악하고 경쟁 구도와 자사 경쟁력을 평가해 향후 전략을 수립하는 시장조사·시장분석 방법을 제시하는 자료입니다.\", \"score\": 0.7726229429244995, \"insight\": \"<기존 아이디어> 시간표 기반 회의 최적화의 시장성 평가와 경쟁 현황 파악이 핵심 요소로 간주됩니다.\\n<내 아이디어> 교육기관 및 팀/조직 대상의 회의 시간 최적화 서비스로서, 시장 규모 추정과 경쟁 구도 분석을 기반으로 차별화 포지션을 설계해야 합니다. 구독형 SaaS 모델이나 교육기관 라이선스 모델 등을 고려해 수익 흐름을 명확히 설정하고, 고객 세그먼트에 따른 가치 제안을 구체화하는 것이 좋겠습니다.\\n(Keyword: N/A, Team Members: N/A, Link: https://repository.kisti.re.kr/bitstream/10580/6312/1/2016-008%20%EC%84%B1%EA%B3%B5%EC%B0%BD%EC%97%85%EC%9D%84%20%EC%9C%84%ED%95%9C%20%EC%8B%9C%EC%9E%A5%EC%A1%B0%EC%82%AC%20%EB%B0%8F%20%EC%8B%9C%EC%9E%A5%EB%B6%84%EC%84%9D.pdf, Source: N/A)\", \"image\": \"\"}, {\"source_type\": \"web\", \"title\": \"경쟁업체를 분석 노하우 - 300cbt | 삼백씨비티\", \"summary\": \"경쟁 분석의 방법론과 절차, 도구를 제시하고 실행 주기까지 구체화하여 경쟁 우위를 확보하는 노하우를 다룬 자료입니다.\", \"score\": 0.7656759023666382, \"insight\": \"<기존 아이디어> 경쟁사 분석의 기본 프레임과 모니터링 주기가 제시되어 있습니다.\\n<내 아이디어> 회의 시간 최적화 서비스에서 경쟁자를 식별할 때 캘린더/일정 관리 도구, 교육용 협업 플랫폼 등을 후보로 삼고, Cross-기능 차별화 포인트를 도출해야 합니다. 예를 들어 timetables와 팀 일정의 교차 분석 가능 여부, 프라이버시 설정, 교육 기관 특화 기능 등을 검토해 차별화 전략을 구체화하십시오.\\n(Keyword: N/A, Team Members: N/A, Link: https://300cbt.com/blogs/ecommerce-marketing/what-is-a-competitive-analysis-how-to-guide?srsltid=AfmBOorwiUYKiLFkavrwxEvhPeH8dWDWLnufX1FeDH63BlZLihGSG6gA, Source: N/A)\", \"image\": \"\"}, {\"source_type\": \"web\", \"title\": \"시장 경쟁자 분석 : 경쟁 우위를 확보합니다\", \"summary\": \"경쟁사 분석의 중요성과 시장 동향, 고객 선호도 파악을 통한 전략 수립의 필요성을 강조하는 자료입니다.\", \"score\": 0.7615815997123718, \"insight\": \"<기존 아이디어> 경쟁 우위 확보를 위한 경쟁사 분석의 중요성을 확인합니다.\\n<내 아이디어> 회의 시간 최적화 서비스를 계획할 때, 교육기관 및 팀 단위의 특성을 반영한 경쟁사 비교표를 작성하고, 데이터 분석 파이프라인을 활용한 차별화 지점(예: 교차 일정 분석, 프라이버시 보안 관리)을 구체화하십시오.\\n(Keyword: N/A, Team Members: N/A, Link: https://www.verifiedmarketresearch.com/ko/blog/understanding-market-competitors-a-comprehensive-analysis-in-market-research/, Source: N/A)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"지속적인 기술 모니터링 및 분석을 통해 기업의 기술 위치 변화와 외부 요인을 신속히 파악하고, 전략적으로 대응할 수 있도록 지원하는 솔루션\", \"summary\": \"클라우드/SaaS 기술 모니터링 및 분석에 관한 외부 사례를 소개하는 자료입니다.\", \"score\": 0.6819478273391724, \"insight\": \"<기존 아이디어> SaaS 기반 회의 최적화 서비스의 기술 위치와 외부 요인 모니터링 필요성에 공감합니다.\\n<내 아이디어> 서비스 운영 측면에서 지속적인 모니터링 및 성능 지표 추적, 업데이트 주기 정의, 벤치마크 관리를 도입해 안정성과 경쟁력을 높이십시오. (Keyword: 클라우드/SaaS, Team Members: 넥스트솔, Link: https://www.data.go.kr/suc/awardWinning.do, Source: 외부)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"스마트 팜 IoT 기반 유기농 작물 재배 솔루션\", \"summary\": \"IoT 센서와 AI를 결합한 스마트팜 시스템으로 실시간 모니터링과 자동 제어를 통해 작물 생산성을 높이는 사례입니다.\", \"score\": 0.40555164217948914, \"insight\": \"<기존 아이디어> 현 아이디어와 직접 연관성은 낮지만 AI/데이터 분석의 응용 가능성을 보여주는 사례입니다.\\n<내 아이디어> 회의 시간 최적화 알고리즘에서도 센서형 데이터(참석 가능 여부, 방문자 가용성 등)와 예측 모델을 도입해 더 정교한 추천을 시도할 수 있습니다. 다만 도메인 차이를 명확히 하고, 개인정보 이슈를 고려해야 합니다.\\n(Keyword: IoT, 스마트팜, 유기농, AI, 농업, Team Members: 류성민, 배진호, 신혜원, Link: /project/user_004, Source: Iideantify 유저)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"AR 기반 가상 화장실 데모 및 쇼핑 서비스\", \"summary\": \"AR 기술을 활용해 공간에 가구 등을 배치해보는 데모 및 쇼핑 서비스 사례입니다.\", \"score\": 0.3760179579257965, \"insight\": \"<기존 아이디어> AR/가상 시각화 기술의 구현 사례를 보여 줍니다.\\n<내 아이디어> 회의 시간 추천 시스템에 AR을 적용하기보다는 UI/UX에서의 직관성 향상이나, 특정 공간(예: 캠퍼스 내 회의실)의 시각화된 일정 배치를 통해 사용 경험을 강화하는 방향으로 참고할 수 있습니다. (Keyword: AR, 가상 데모, 인테리어, 쇼핑, Team Members: 박수빈, 최민석, 김나연, Link: /project/user_008, Source: Iideantify 유저)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"실시간 식품 안전성 검증 블록체인 플랫폼\", \"summary\": \"블록체인 기술로 생산자-���비자 간의 식품 이력 및 안전 정보를 추적하는 플랫폼 사례입니다.\", \"score\": 0.3491707444190979, \"insight\": \"<기존 아이디어> 데이터 투명성과 신뢰성 구축의 중요성을 시사합니다.\\n<내 아이디어> 회의 일정 데이터의 무결성과 접근 제어를 강화하는 보안 모델을 고려해 신뢰성 있는 협업 플랫폼으로의 확장을 모색해 보십시오. (Keyword: 식품 안전, 블록체인, 공급망 추적, 투명성, Team Members: 정수진, 최동욱, Link: /project/user_002, Source: Iideantify 유저)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"대학생을 위한 팀플 일정 관리 및 협업 도구\", \"summary\": \"대학생 팀 프로젝트를 위한 일정 관리 및 협업 도구로, 캘린더 연동을 통해 팀원 일정 분석 및 최적의 회의 시간을 제안하는 기능을 제공합니다.\", \"score\": 0.3402193784713745, \"insight\": \"<기존 아이디어> 대학생 대상의 팀 프로젝트 협업 도구와 일정 최적화의 구체적 사례를 보여 줍니다.\\n<내 아이디어> 본 아이디어의 핵심과 가장 유사한 사례로, 캘린더 데이터 기반 회의 시간 제안 및 팀 단위 최적화에 대한 실전적 벤치마크를 제공합니다. 이를 바탕으로 교육기관용 확장, 프라이버시 옵션 강화, 과제 트래킹 연계 등 확장 방향을 모색하십시오. (Keyword: 팀플, 일정 관리, 협업, 대학생, Team Members: 강미영, 윤태현, 한지우, 오서연, Link: /project/user_003, Source: Iideantify 유저)\", \"image\": \"\"}, {\"source_type\": \"internal_db\", \"title\": \"AI 기반 개인 맞춤형 영양 관리 플랫폼\", \"summary\": \"사용자의 건강 상태와 생활 습관을 분석해 AI가 맞춤형 영양 계획을 제공하는 모바일 애플리케이션 사례입니다.\", \"score\": 0.32036375999450684, \"insight\": \"<기존 아이디어> AI 기반 개인화 추천 시스템의 구현 가능성을 확인합니다.\\n<내 아이디어> 일정 추천 알고리즘에 개인화 요소를 도입하되, 팀 구성원별 선호도나 작업 우선순위에 따른 맞춤형 제안으로 확장할 수 있습니다. 다만 도메인 특성상 개인정보 보호와 데이터 최소 수집 원칙을 엄격히 적용해야 합니다. (Keyword: AI, 영양 관리, 건강 관리, 모바일 앱, Team Members: 김철수, 이영희, 박민수, Link: /project/user_001, Source: Iideantify 유저)\", \"image\": \"\"}]}}}";
//
//        onResponse(
//                org.springframework.messaging.support.MessageBuilder.createMessage(MOCK_DATA, new MessageHeaders(Map.of()))
//        );
//    }
}
