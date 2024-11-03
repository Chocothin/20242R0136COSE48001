package com.example.choco_planner.storage.repository;

import com.example.choco_planner.storage.entity.RecordingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RecordingRepository extends JpaRepository<RecordingEntity, Long> {

}
