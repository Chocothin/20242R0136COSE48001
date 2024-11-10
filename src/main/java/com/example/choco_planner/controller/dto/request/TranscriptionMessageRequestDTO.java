package com.example.choco_planner.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranscriptionMessageRequestDTO {

    // 클라이언트에서 전송하는 Base64 인코딩된 오디오 데이터를 받을 필드
    private String audioData;
    private Long classId;
    private Long userId;

    // 기본 생성자
    public TranscriptionMessageRequestDTO() {}

    public TranscriptionMessageRequestDTO(String audioData, Long classId, Long userId) {
        this.audioData = audioData;
        this.classId = classId;
        this.userId = userId;
    }


}

