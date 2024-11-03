package com.example.choco_planner.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recording")
public class RecordingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "transcript")
    private String transcript;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

}
