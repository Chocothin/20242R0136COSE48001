package com.example.choco_planner.controller;

import com.example.choco_planner.controller.dto.response.RecordingDetailResponseDTO;
import com.example.choco_planner.controller.dto.response.RecordingResponseDTO;
import com.example.choco_planner.service.RecordingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Recording", description = "Recording API")
@RestController
@RequestMapping("/api/recording")
@RequiredArgsConstructor
public class RecordingController {

    private final RecordingService recordingService;

    // 특정 유저의 수업 id에 해당하는 모든 recording을 반환
    @Operation(summary = "특정 유저의 수업 id에 해당하는 모든 recording을 반환")
    @GetMapping("/{classId}")
    public List<RecordingResponseDTO> getRecordings(
            @PathVariable Long classId
    ) {
        return recordingService.getRecordings(classId);
    }

    @Operation(summary = "특정 recording의 상세 정보 반환")
    @GetMapping("/detail/{recordingId}")
    public RecordingDetailResponseDTO getRecordingDetail(
            @PathVariable Long recordingId
    ) {
        return recordingService.getRecordingDetail(recordingId);
    }


}
