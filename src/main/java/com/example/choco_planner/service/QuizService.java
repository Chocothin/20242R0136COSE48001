package com.example.choco_planner.service;

import com.example.choco_planner.storage.entity.QuizEntity;
import com.example.choco_planner.storage.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {
    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public List<QuizEntity> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public QuizEntity getQuizById(int id) {
        return quizRepository.findById(id).orElse(null);
    }

    public QuizEntity saveQuiz(QuizEntity quiz) {
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(int id) {
        quizRepository.deleteById(id);
    }
}
