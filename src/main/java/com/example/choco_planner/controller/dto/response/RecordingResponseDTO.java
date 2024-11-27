package com.example.choco_planner.controller.dto.response;

import com.example.choco_planner.storage.entity.RecordingDetailEntity;
import com.example.choco_planner.storage.entity.RecordingEntity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RecordingResponseDTO {

    private Long recordingId;
    private String recordingTitle;
    private Long classId;
    private String classTitle;
    private String preTranscript;
    private LocalDateTime recordedAt;
    private Integer durationMinutes;

    public static RecordingResponseDTO fromEntity(RecordingEntity recordingEntity) {
        RecordingResponseDTO dto = new RecordingResponseDTO();
        dto.recordingId = recordingEntity.getId();
        dto.recordingTitle = recordingEntity.getTitle();
        dto.classId = recordingEntity.getClassEntity().getId();
        dto.classTitle = recordingEntity.getClassEntity().getTitle(); // ClassEntity에서 title 가져오기
        dto.recordedAt = recordingEntity.getRecordedAt();
        dto.durationMinutes = recordingEntity.getStoppedAt() != null ?
                (int) (recordingEntity.getStoppedAt().getMinute() - recordingEntity.getRecordedAt().getMinute()) :
                null;
        // Get the first transcript (if exists) and trim to 100 characters
        if (recordingEntity.getDetails() != null && !recordingEntity.getDetails().isEmpty()) {
            RecordingDetailEntity firstDetail = recordingEntity.getDetails().get(0);
            if (firstDetail.getTranscript() != null) {
                dto.preTranscript = firstDetail.getTranscript().substring(0, Math.min(100, firstDetail.getTranscript().length()));
            }
        }

        return dto;
    }

    public static List<RecordingResponseDTO> fromEntities(List<RecordingEntity> recordingEntities) {
        return recordingEntities.stream()
                .map(RecordingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
