package com.example.choco_planner.controller.dto.response;

public class TranscribedMessageResponseDTO {
    private String message;

    public TranscribedMessageResponseDTO() {
    }

    public TranscribedMessageResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
