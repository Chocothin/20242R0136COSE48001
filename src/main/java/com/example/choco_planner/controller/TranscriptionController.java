package com.example.choco_planner.controller;

import com.example.choco_planner.controller.dto.request.TranscriptionMessageRequestDTO;
import com.example.choco_planner.service.RealTimeSTTService;
import com.example.choco_planner.service.RecordingService;
import com.example.choco_planner.service.SpeechToTextService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transcription", description = "Transcription API")
@RestController
@RequestMapping("/transcription")
public class TranscriptionController {

    private final RealTimeSTTService realTimeSTTService;
    private final SpeechToTextService speechToTextService;
    private final RecordingService recordingService;

    public TranscriptionController(RealTimeSTTService realTimeSTTService, SpeechToTextService speechToTextService, RecordingService recordingService) {
        this.realTimeSTTService = realTimeSTTService;
        this.speechToTextService = speechToTextService;
        this.recordingService = recordingService;
    }

    @MessageMapping("/voice")
    @SendTo("/topic/translated")
    public String handleVoiceMessage(
            TranscriptionMessageRequestDTO message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            // 세션 ID를 클라이언트 식별자로 사용
            String sessionId = headerAccessor.getSessionId();
            System.out.println("세션 ID: " + sessionId);
            System.out.println("수신된 audioData: " + message.getAudioData());

            // Base64 데이터를 음성 텍스트 변환 서비스로 전달
            return speechToTextService.processAudioChunk(
                    sessionId,
                    message.getAudioData(),
                    message.getClassId(),
                    message.getUserId()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
