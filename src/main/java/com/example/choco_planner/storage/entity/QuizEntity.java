package com.example.choco_planner.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recording_id", nullable = false)
    private Long recordingId;

    @Column(nullable = false)
    private String quiz;

    @Column(nullable = true)
    private String answer;
}
