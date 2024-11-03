package com.example.choco_planner.controller;

import com.example.choco_planner.service.RecordingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recording", description = "Recording API")
@RestController
@RequestMapping("/api/recording")
@RequiredArgsConstructor
public class RecordingController {

    private final RecordingService recordingService;
}
