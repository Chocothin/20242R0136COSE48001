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
@Table(name = "recording_detail")
public class RecordingDetailEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recording_id", nullable = false)
    private RecordingEntity recording;

    @Column(name = "transcript")
    private String transcript;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

}
