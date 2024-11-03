package com.example.choco_planner.storage.repository;

import com.example.choco_planner.storage.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    List<ClassEntity> findByUserId(Long userId);
    Optional<ClassEntity> findByUserIdAndId(Long userId, Long classId);
}
