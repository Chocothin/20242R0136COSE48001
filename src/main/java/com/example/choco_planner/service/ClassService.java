package com.example.choco_planner.service;

import com.example.choco_planner.common.domain.CustomUser;
import com.example.choco_planner.controller.dto.request.CreateClassRequestDTO;
import com.example.choco_planner.controller.dto.response.ClassResponseDTO;
import com.example.choco_planner.storage.entity.ClassEntity;
import com.example.choco_planner.storage.repository.ClassRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassService {
    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @Transactional
    public ClassResponseDTO createClass(
            CreateClassRequestDTO dto,
            Long userId
    ) {
        ClassEntity classEntity = ClassEntity.builder()
                .userId(userId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();

        ClassEntity savedClass = classRepository.save(classEntity);

        return ClassResponseDTO.builder()
                .id(savedClass.getId())
                .userId(savedClass.getUserId())
                .title(savedClass.getTitle())
                .description(savedClass.getDescription())
                .createdAt(savedClass.getCreatedAt())
                .updatedAt(savedClass.getUpdatedAt())
                .build();
    }

    // 특정 사용자(userId)의 모든 수업 조회 메서드
    public List<ClassResponseDTO> getClassesByUserId(Long userId) {
        return classRepository.findByUserId(userId)
                .stream()
                .map(ClassResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 사용자(userId)와 특정 수업(classId) 조회 메서드
    public Optional<ClassResponseDTO> getClassByClassId(Long classId) {
        return classRepository.findById(classId)
                .map(ClassResponseDTO::fromEntity);
    }
}
