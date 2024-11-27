package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.RecordingDetailResponseDTO;
import com.example.choco_planner.controller.dto.response.RecordingResponseDTO;
import com.example.choco_planner.storage.entity.ClassEntity;
import com.example.choco_planner.storage.entity.RecordingEntity;
import com.example.choco_planner.storage.repository.ClassRepository;
import com.example.choco_planner.storage.repository.RecordingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordingService {

    private final RecordingRepository recordingRepository;
    private final ClassRepository classRepository;

    /**
     * 녹음 데이터를 저장하는 메서드
     *
     * @param userId  사용자 ID
     * @param classId 클래스 ID
     * @return 저장된 녹음 ID
     */
    public RecordingEntity saveRecording(Long userId, Long classId) {
        // ClassEntity 조회
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("클래스를 찾을 수 없습니다. ID: " + classId));

        // RecordingEntity 생성 및 저장
        RecordingEntity entity = RecordingEntity.builder()
                .userId(userId)
                .classEntity(classEntity)
                .title(LocalDateTime.now().toString() + " 녹음")
                .recordedAt(LocalDateTime.now())
                .build();

        return recordingRepository.save(entity);
    }

    public void updateStoppedAt(Long recordingId) {
        RecordingEntity recordingEntity = recordingRepository.findById(recordingId)
                .orElseThrow(() -> new IllegalArgumentException("녹음을 찾을 수 없습니다. ID: " + recordingId));
        recordingEntity.setStoppedAt(LocalDateTime.now());
        recordingRepository.save(recordingEntity);
    }

    /**
     * 클래스 ID로 녹음 목록을 조회하는 메서드
     *
     * @param classId 클래스 ID
     * @return 녹음 응답 DTO 리스트
     */
    public List<RecordingResponseDTO> getRecordings(Long classId) {
        // 클래스에 속한 녹음 목록 조회
        List<RecordingEntity> recordingEntities = recordingRepository.findAllByClassEntity_Id(classId);
        return RecordingResponseDTO.fromEntities(recordingEntities);
    }

    /**
     * 녹음 ID로 녹음 상세 정보를 조회하는 메서드
     *
     * @param recordingId 녹음 ID
     * @return 녹음 응답 DTO
     */
    public RecordingDetailResponseDTO getRecordingDetail(Long recordingId) {
        // 녹음 ID로 녹음 조회
        RecordingEntity recordingEntity = recordingRepository.findById(recordingId)
                .orElseThrow(() -> new IllegalArgumentException("녹음을 찾을 수 없습니다. ID: " + recordingId));
        return RecordingDetailResponseDTO.fromEntity(recordingEntity);
    }

    public RecordingEntity getRecording(Long recordingId) {
        return recordingRepository.findById(recordingId).orElseThrow(
                () -> new IllegalArgumentException("녹음을 찾을 수 없습니다. ID: " + recordingId)
        );
    }
}
