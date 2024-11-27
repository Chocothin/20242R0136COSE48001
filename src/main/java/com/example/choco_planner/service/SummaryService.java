package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.RecordingDetailResponseDTO;
import com.example.choco_planner.storage.entity.RecordingDetailEntity;
import com.example.choco_planner.storage.entity.SummaryEntity;
import com.example.choco_planner.storage.repository.SummaryRepository;
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

    public List<String> generateSummary(Long userId, Long recordingId) {
        List<String> texts = recordingService.getRecording(recordingId)
                .getDetails()
                .stream()
                .map(
                        RecordingDetailEntity::getTranscript
                )
                .toList();
        String text = String.join("\n", texts);
        String summary = openAiTextService.generateSummary(text);
        saveSummary(userId, recordingId, summary);
        return List.of(summary);
//        List<String> summaries = recordingDetailService.getRecordingDetails(recordingId)
//                .stream().map(RecordingDetailResponseDTO::getTranscript)
//                .map(openAiTextService::generateSummary).toList();
//        summaries.forEach(summary -> saveSummary(userId, recordingId, summary));
//        return summaries;
    }

    private void saveSummary(Long userId, Long recordingId, String summaryText) {
        SummaryEntity summaryEntity = SummaryEntity.builder()
                .recordingId(recordingId)
                .userId(userId)
                .summaryText(summaryText)
                .build();
        summaryRepository.save(summaryEntity);
    }

    public List<String> getSummary(Long userId, Long recordingId) {
        return summaryRepository.findByRecordingIdAndUserId(recordingId, userId)
                .stream()
                .map(SummaryEntity::getSummaryText)
                .toList();
    }
}
