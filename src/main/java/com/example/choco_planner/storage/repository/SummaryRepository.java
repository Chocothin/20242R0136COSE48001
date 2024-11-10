package com.example.choco_planner.storage.repository;

import com.example.choco_planner.storage.entity.SummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryRepository extends JpaRepository<SummaryEntity, Integer> {

    List<SummaryEntity> findByClassIdAndUserId(Long classId, Long userId);
}
