package com.example.choco_planner.controller.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 최상위 클래스
@Getter
@Setter
public class SummaryResponse {
    private List<Chapter> response; // 기존 응답 필드
    private List<QuizAndAnswerDTO> quizResponse; // 새로운 필드 추가

    @Override
    public String toString() {
        return "SummaryResponse{" +
                "response=" + response +
                ", quizResponse=" + quizResponse +
                '}';
    }
}
