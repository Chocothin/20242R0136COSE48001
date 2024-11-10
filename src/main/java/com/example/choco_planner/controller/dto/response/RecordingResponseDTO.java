package com.example.choco_planner.controller.dto.response;

import com.example.choco_planner.storage.entity.RecordingEntity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RecordingResponseDTO {
    // Getters and possibly setters if needed
    private Long id;
    private Long classId;
    private String transcript;
    private LocalDateTime recordedAt;

    public static RecordingResponseDTO fromEntity(RecordingEntity recordingEntity) {
        RecordingResponseDTO dto = new RecordingResponseDTO();
        dto.id = recordingEntity.getId();
        dto.classId = recordingEntity.getClassId();
        dto.transcript = recordingEntity.getTranscript();
        dto.recordedAt = recordingEntity.getRecordedAt();
        return dto;
    }

    public static List<RecordingResponseDTO> fromEntities(List<RecordingEntity> recordingEntities) {
        return recordingEntities.stream()
                .map(RecordingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

}
