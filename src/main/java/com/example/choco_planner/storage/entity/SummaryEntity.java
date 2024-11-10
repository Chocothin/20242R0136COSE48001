package com.example.choco_planner.storage.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "summary")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SummaryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "summary_text", nullable = false)
    private String summaryText;

}
