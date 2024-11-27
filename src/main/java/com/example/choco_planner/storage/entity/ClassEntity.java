package com.example.choco_planner.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    private String description;

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RecordingEntity> recordings; // 1:N 관계 설정
}

