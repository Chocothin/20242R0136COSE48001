package com.example.choco_planner.controller.dto.response;

import java.util.List;

// 최상위 클래스
public class SummaryResponse {
    private List<Chapter> response;

    public List<Chapter> getResponse() {
        return response;
    }

    public void setResponse(List<Chapter> response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "SummaryResponse{" +
                "response=" + response +
                '}';
    }
}

