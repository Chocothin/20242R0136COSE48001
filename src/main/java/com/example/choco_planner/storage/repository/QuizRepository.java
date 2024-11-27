package com.example.choco_planner.storage.repository;

import com.example.choco_planner.storage.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Integer> {

    List<QuizEntity> findByRecordingIdAndUserId(Long recordingId, Long userId);
}
