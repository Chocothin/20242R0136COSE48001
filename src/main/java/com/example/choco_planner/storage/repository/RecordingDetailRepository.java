package com.example.choco_planner.storage.repository;

import com.example.choco_planner.storage.entity.RecordingDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordingDetailRepository extends JpaRepository<RecordingDetailEntity, Long> {

    List<RecordingDetailEntity> findAllByRecordingId(Long recordingId);
}
