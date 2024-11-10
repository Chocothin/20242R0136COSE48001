package com.example.choco_planner.service.vo.response;

import com.example.choco_planner.storage.entity.QuizEntity;

public record QuizAndAnswerVO(Long quizId, String quiz, String answer) {

        public static QuizAndAnswerVO fromEntity(QuizEntity quizEntity) {
            return new QuizAndAnswerVO(quizEntity.getId(), quizEntity.getQuiz(), quizEntity.getAnswer());
        }
}
