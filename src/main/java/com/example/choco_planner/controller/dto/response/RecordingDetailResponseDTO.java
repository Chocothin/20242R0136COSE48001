package com.example.choco_planner.controller.dto.response;

import com.example.choco_planner.common.utils.TimeFormatter;
import com.example.choco_planner.storage.entity.RecordingDetailEntity;
import com.example.choco_planner.storage.entity.RecordingEntity;
import lombok.Getter;

import java.util.List;

@Getter
public class RecordingDetailResponseDTO {
    private Long recordingId;
    private String recordingTitle;
    private Long classId;
    private String classTitle;
    private List<Transcription> transcripts;

    public static RecordingDetailResponseDTO fromEntity(RecordingEntity recordingEntity) {
        RecordingDetailResponseDTO dto = new RecordingDetailResponseDTO();
        dto.recordingId = recordingEntity.getId();
        dto.recordingTitle = recordingEntity.getTitle();
        dto.classId = recordingEntity.getClassEntity().getId();
        dto.classTitle = recordingEntity.getClassEntity().getTitle(); // ClassEntity에서 title 가져오기

        List<RecordingDetailEntity> details = recordingEntity.getDetails();
        dto.transcripts = details.stream()
                .map(detail -> {
                    Transcription transcription = new Transcription(
                            detail.getTranscript(),
                            TimeFormatter.formatRecordingTimestamp(detail.getRecordedAt(), recordingEntity.getCreatedAt())
                    );
                    return transcription;
                }).toList();
        return dto;
    }
}

