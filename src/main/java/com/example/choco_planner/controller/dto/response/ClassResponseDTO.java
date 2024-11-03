package com.example.choco_planner.controller.dto.response;

import com.example.choco_planner.storage.entity.ClassEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassResponseDTO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClassResponseDTO fromEntity(ClassEntity classEntity) {
        return ClassResponseDTO.builder()
                .id(classEntity.getId())
                .userId(classEntity.getUserId())
                .title(classEntity.getTitle())
                .description(classEntity.getDescription())
                .createdAt(classEntity.getCreatedAt())
                .updatedAt(classEntity.getUpdatedAt())
                .build();
    }
}
