package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.RecordingDetailResponseDTO;
import com.example.choco_planner.storage.entity.RecordingDetailEntity;
import com.example.choco_planner.storage.entity.RecordingEntity;
import com.example.choco_planner.storage.repository.RecordingDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordingDetailService {
    private final RecordingDetailRepository recordingDetailRepository;

    public void saveRecordingDetail(
            RecordingEntity recording,
            String transcript
    ) {
        RecordingDetailEntity entity = RecordingDetailEntity.builder()
                .recording(recording)
                .transcript(transcript)
                .recordedAt(LocalDateTime.now())
                .build();
        recordingDetailRepository.save(entity);
    }
}
