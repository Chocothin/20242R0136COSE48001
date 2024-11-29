package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.RecordingDetailResponseDTO;
import com.example.choco_planner.controller.dto.response.SummaryResponse;
import com.example.choco_planner.storage.entity.RecordingDetailEntity;
import com.example.choco_planner.storage.entity.SummaryEntity;
import com.example.choco_planner.storage.repository.SummaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

    private final OpenAiTextService openAiTextService;
    private final RecordingService recordingService;
    private final SummaryRepository summaryRepository;
    private final RecordingDetailService recordingDetailService;


    public SummaryService(
            OpenAiTextService openAiTextService,
            RecordingService recordingService,
            SummaryRepository summaryRepository,
            RecordingDetailService recordingDetailService
    ) {
        this.openAiTextService = openAiTextService;
        this.recordingService = recordingService;
        this.summaryRepository = summaryRepository;
        this.recordingDetailService = recordingDetailService;
    }

    public SummaryResponse generateSummary(Long userId, Long recordingId) {
        // 녹음 텍스트 가져오기
        List<String> texts = recordingService.getRecording(recordingId)
                .getDetails()
                .stream()
                .map(RecordingDetailEntity::getTranscript)
                .toList();

        // 텍스트 결합
        String text = String.join("\n", texts);

        // OpenAI API로 요약 요청
        SummaryResponse summaryResponse = openAiTextService.generateSummary(text);

        // 요약 결과 저장
        saveSummary(userId, recordingId, summaryResponse);

        // 요약 결과 반환
        return summaryResponse;
    }

    private void saveSummary(Long userId, Long recordingId, SummaryResponse summaryResponse) {
        try {
            // SummaryResponse 객체를 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String summaryText = objectMapper.writeValueAsString(summaryResponse);

            // SummaryEntity 생성 및 저장
            SummaryEntity summaryEntity = SummaryEntity.builder()
                    .recordingId(recordingId)
                    .userId(userId)
                    .summaryText(summaryText)
                    .build();
            summaryRepository.save(summaryEntity);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("요약 저장 실패", e);
        }
    }

    public SummaryResponse getSummary(Long userId, Long recordingId) {
        ObjectMapper objectMapper = new ObjectMapper();

        // SummaryEntity 조회
        SummaryEntity summaryEntity = summaryRepository.findFirstByRecordingIdAndUserIdOrderByIdDesc(recordingId, userId);

        if (summaryEntity == null) {
            throw new RuntimeException("Summary not found for recordingId: " + recordingId + ", userId: " + userId);
        }

        try {
            // JSON 문자열을 SummaryResponse 객체로 변환
            return objectMapper.readValue(summaryEntity.getSummaryText(), SummaryResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SummaryResponse 변환 실패", e);
        }
    }
}
