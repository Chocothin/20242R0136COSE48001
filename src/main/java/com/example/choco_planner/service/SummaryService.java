package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.RecordingResponseDTO;
import com.example.choco_planner.storage.entity.SummaryEntity;
import com.example.choco_planner.storage.repository.SummaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

    private final OpenAiTextService openAiTextService;
    private final RecordingService recordingService;
    private final SummaryRepository summaryRepository;

    public SummaryService(OpenAiTextService openAiTextService, RecordingService recordingService, SummaryRepository summaryRepository) {
        this.openAiTextService = openAiTextService;
        this.recordingService = recordingService;
        this.summaryRepository = summaryRepository;
    }

    public List<String> generateSummary(Long userId, Long classId) {
        List<String> summaries = recordingService.getRecordings(classId)
                .stream().map(RecordingResponseDTO::getTranscript)
                .map(openAiTextService::generateSummary).toList();
        summaries.forEach(summary -> saveSummary(userId, classId, summary));
        return summaries;
    }

    private void saveSummary(Long userId, Long classId, String summaryText) {
        SummaryEntity summaryEntity = SummaryEntity.builder()
                .classId(classId)
                .userId(userId)
                .summaryText(summaryText)
                .build();
        summaryRepository.save(summaryEntity);
    }

    public List<String> getSummary(Long userId, Long classId) {
        return summaryRepository.findByClassIdAndUserId(classId, userId)
                .stream()
                .map(SummaryEntity::getSummaryText)
                .toList();
    }
}
