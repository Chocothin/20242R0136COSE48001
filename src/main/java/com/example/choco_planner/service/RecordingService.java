package com.example.choco_planner.service;

import com.example.choco_planner.common.domain.CustomUser;
import com.example.choco_planner.controller.dto.response.RecordingResponseDTO;
import com.example.choco_planner.storage.entity.RecordingEntity;
import com.example.choco_planner.storage.repository.RecordingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordingService {
    private final RecordingRepository recordingRepository;

    public void saveRecording(
            Long userId,
            Long classId,
            String transcript
    ) {
        RecordingEntity entity = RecordingEntity.builder()
                .userId(userId)
                .classId(classId)
                .transcript(transcript)
                .recordedAt(LocalDateTime.now())
                .build();
        recordingRepository.save(entity);
    }

    public List<RecordingResponseDTO> getRecordings(Long classId) {
        List<RecordingEntity> recordingEntities = recordingRepository.findByClassId(classId);
        return RecordingResponseDTO.fromEntities(recordingEntities);

    }
}
