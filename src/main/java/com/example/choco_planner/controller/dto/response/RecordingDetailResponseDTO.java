package com.example.choco_planner.controller.dto.response;

import com.example.choco_planner.storage.entity.RecordingDetailEntity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RecordingDetailResponseDTO {
    private Long recordingId;
    private String recordingTitle;
    private Long classId;
    private String classTitle; // ClassEntity의 title 필드
    private String transcript;
    private LocalDateTime recordedAt;

    public static RecordingDetailResponseDTO fromEntity(RecordingDetailEntity recordingDetailEntity) {
        RecordingDetailResponseDTO dto = new RecordingDetailResponseDTO();
        dto.recordingId = recordingDetailEntity.getRecording().getId();
        dto.recordingTitle = recordingDetailEntity.getRecording().getTitle();
        dto.classId = recordingDetailEntity.getRecording().getClassEntity().getId();
        dto.classTitle = recordingDetailEntity.getRecording().getClassEntity().getTitle(); // ClassEntity에서 title 가져오기
        dto.recordedAt = recordingDetailEntity.getRecordedAt();
        dto.transcript = recordingDetailEntity.getTranscript();
        return dto;
    }

    public static List<RecordingDetailResponseDTO> fromEntities(List<RecordingDetailEntity> recordingDetailEntities) {
        return recordingDetailEntities.stream()
                .map(RecordingDetailResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
