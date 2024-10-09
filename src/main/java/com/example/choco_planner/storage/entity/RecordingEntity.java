package com.example.choco_planner.storage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "recording")
public class RecordingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "transcribed_text")
    private String transcribedText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RecordingEntity() {}

    public RecordingEntity(String filePath, String transcribedText) {
        this.filePath = filePath;
        this.transcribedText = transcribedText;
        this.createdAt = LocalDateTime.now();
    }
}
