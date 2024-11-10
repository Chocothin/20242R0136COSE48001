package com.example.choco_planner.controller;

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
    @PostMapping("/{userId}/{classId}")
    public List<QuizAndAnswerVO> generateQuiz(
            @PathVariable Long userId,
            @PathVariable Long classId
    ) {
        return quizService.generateQuiz(userId, classId);
    }

    @Operation(summary = "class의 퀴즈 조회")
    @GetMapping("/{userId}/{classId}")
    public List<QuizAndAnswerVO> getQuiz(
            @PathVariable Long userId,
            @PathVariable Long classId
    ) {
        return quizService.getQuiz(userId, classId);
    }
}
