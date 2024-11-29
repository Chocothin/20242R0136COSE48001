package com.example.choco_planner.controller.dto.response;

import com.example.choco_planner.storage.entity.QuizEntity;

public class QuizAndAnswerDTO {
    private String quiz;
    private String answer;

    // 기본 생성자
    public QuizAndAnswerDTO() {}

    public QuizAndAnswerDTO(String quiz, String answer) {
        this.quiz = quiz;
        this.answer = answer;
    }

    // Getters and Setters
    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public static QuizAndAnswerDTO fromEntity(QuizEntity quizEntity) {
        return new QuizAndAnswerDTO(quizEntity.getQuiz(), quizEntity.getAnswer());
    }
}
