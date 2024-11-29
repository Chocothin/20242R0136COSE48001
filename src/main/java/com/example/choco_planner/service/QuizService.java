package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.QuizAndAnswerDTO;
import com.example.choco_planner.controller.dto.response.RecordingDetailResponseDTO;
import com.example.choco_planner.service.vo.response.QuizAndAnswerVO;
import com.example.choco_planner.storage.entity.QuizEntity;
import com.example.choco_planner.storage.entity.RecordingDetailEntity;
import com.example.choco_planner.storage.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final OpenAiTextService openAiTextService;
    private final RecordingService recordingService;
    private final RecordingDetailService recordingDetailService;

    public QuizService(
            QuizRepository quizRepository,
            OpenAiTextService openAiTextService,
            RecordingService recordingService,
            RecordingDetailService recordingDetailService
    ) {
        this.quizRepository = quizRepository;
        this.openAiTextService = openAiTextService;
        this.recordingService = recordingService;
        this.recordingDetailService = recordingDetailService;
    }

    public List<QuizAndAnswerDTO> generateQuiz(Long userId, Long recordingId) {
        List<String> texts =  recordingService.getRecording(recordingId)
                .getDetails()
                .stream()
                .map(RecordingDetailEntity::getTranscript)
                .toList();

        String text = String.join("\n", texts);

        List<QuizAndAnswerDTO> quizAndAnswers = openAiTextService.generateQuiz(text);

        quizAndAnswers.forEach(quizAndAnswer -> saveQuiz(userId, recordingId, quizAndAnswer));

        return quizAndAnswers;
    }


    private QuizEntity saveQuiz(Long userId, Long recordingId, QuizAndAnswerDTO quizAndAnswer) {
        QuizEntity quizEntity = QuizEntity.builder()
                .recordingId(recordingId)
                .userId(userId)
                .quiz(quizAndAnswer.getQuiz())
                .answer(quizAndAnswer.getAnswer())
                .build();
        return quizRepository.save(quizEntity);
    }



    public List<QuizAndAnswerDTO> getQuiz(Long userId, Long recordingId) {
        List<QuizEntity> quizEntities = quizRepository.findByRecordingIdAndUserId(recordingId,userId);
        return quizEntities.stream()
                .map(QuizAndAnswerDTO::fromEntity)
                .toList();
    }

}
