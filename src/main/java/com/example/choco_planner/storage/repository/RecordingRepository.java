package com.example.choco_planner.storage.repository;

import com.example.choco_planner.storage.entity.RecordingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordingRepository extends JpaRepository<RecordingEntity, Long> {

    List<RecordingEntity> findAllByClassEntity_Id(Long classId);


}
