package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.RecordingResponseDTO;
import com.example.choco_planner.service.vo.response.QuizAndAnswerVO;
import com.example.choco_planner.storage.entity.QuizEntity;
import com.example.choco_planner.storage.repository.QuizRepository;
import com.example.choco_planner.storage.repository.SummaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final OpenAiTextService openAiTextService;
    private final RecordingService recordingService;

    public QuizService(QuizRepository quizRepository, OpenAiTextService openAiTextService, RecordingService recordingService) {
        this.quizRepository = quizRepository;
        this.openAiTextService = openAiTextService;
        this.recordingService = recordingService;
    }

    public List<QuizAndAnswerVO> generateQuiz(Long userId, Long classId) {
        return recordingService.getRecordings(classId)
                .stream().map(RecordingResponseDTO::getTranscript)
                .map(openAiTextService::generateQuiz)
                .map(quiz -> {
                    QuizEntity savedQuiz = saveQuiz(userId, classId, quiz);
                    return new QuizAndAnswerVO(savedQuiz.getId(), quiz.quiz(), quiz.answer());
                }).toList();
    }


    private QuizEntity saveQuiz(Long userId, Long classId, QuizAndAnswerVO quizAndAnswer) {
        QuizEntity quizEntity = QuizEntity.builder()
                .classId(classId)
                .userId(userId)
                .quiz(quizAndAnswer.quiz())
                .answer(quizAndAnswer.answer())
                .build();
        return quizRepository.save(quizEntity);
    }



    public List<QuizAndAnswerVO> getQuiz(Long userId, Long classId) {
        List<QuizEntity> quizEntities = quizRepository.findByClassIdAndUserId(classId,userId);
        return quizEntities.stream().map(QuizAndAnswerVO::fromEntity).toList();
    }

}
