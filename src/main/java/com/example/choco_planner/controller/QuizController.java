package com.example.choco_planner.controller;

import com.example.choco_planner.controller.dto.response.QuizAndAnswerDTO;
import com.example.choco_planner.service.QuizService;
import com.example.choco_planner.service.vo.response.QuizAndAnswerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quiz", description = "Quiz API")
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @Operation(summary = "recording의 퀴즈 생성")
    @PostMapping("/{userId}/{recordingId}")
    public List<QuizAndAnswerDTO> generateQuiz(
            @PathVariable Long userId,
            @PathVariable Long recordingId
    ) {
        return quizService.generateQuiz(userId, recordingId);
    }

    @Operation(summary = "class의 퀴즈 조회")
    @GetMapping("/{userId}/{recordingId}")
    public List<QuizAndAnswerDTO> getQuiz(
            @PathVariable Long userId,
            @PathVariable Long recordingId
    ) {
        return quizService.getQuiz(userId, recordingId);
    }
}
