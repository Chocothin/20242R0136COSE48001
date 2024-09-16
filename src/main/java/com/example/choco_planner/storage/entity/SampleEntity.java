package com.example.choco_planner.storage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sample")
@Getter
@Setter
public class SampleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    // 이름을 인자로 받는 생성자 (Lombok을 사용하지 않는 커스텀 생성자)
    public SampleEntity(String name) {
        this.name = name;
    }

    public SampleEntity() {
    }
}
