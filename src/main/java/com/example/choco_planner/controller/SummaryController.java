package com.example.choco_planner.controller;

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

    @Operation(summary = "recording의 summary를 반환")
    @PostMapping("/{userId}/{classId}")
    public List<String> generateSummary(
            @PathVariable Long userId,
            @PathVariable Long classId
    ) {
        return summaryService.generateSummary(userId, classId);
    }

    @Operation(summary = "class의 summary 조회")
    @GetMapping("/{userId}/{classId}")
    public List<String> getSummary(
            @PathVariable Long userId,
            @PathVariable Long classId
    ) {
        return summaryService.getSummary(userId, classId);
    }

}
