package com.example.choco_planner.controller;

import com.example.choco_planner.controller.dto.response.Chapter;
import com.example.choco_planner.controller.dto.response.QuizAndAnswerDTO;
import com.example.choco_planner.controller.dto.response.SummaryResponse;
import com.example.choco_planner.service.QuizService;
import com.example.choco_planner.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Summary", description = "Summary API")
@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;
    private final QuizService quizService;

    @Operation(summary = "recording의 summary를 반환")
    @PostMapping("/{userId}/{recordingId}")
    public SummaryResponse generateSummary(
            @PathVariable Long userId,
            @PathVariable Long recordingId
    ) {
        // SummaryResponse 생성
        SummaryResponse summary = summaryService.generateSummary(userId, recordingId);

        // QuizAndAnswerDTO 리스트 생성
        List<QuizAndAnswerDTO> quiz = quizService.generateQuiz(userId, recordingId);

        // SummaryResponse에 quizResponse 설정
        summary.setQuizResponse(quiz);

        return summary;
    }


    @Operation(summary = "class의 summary 조회")
    @GetMapping("/{userId}/{recordingId}")
    public SummaryResponse getSummary(
            @PathVariable Long userId,
            @PathVariable Long recordingId
    ) {
        return summaryService.getSummary(userId, recordingId);
    }

}
