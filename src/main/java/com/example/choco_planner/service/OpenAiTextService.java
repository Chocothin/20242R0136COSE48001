package com.example.choco_planner.service;

import com.example.choco_planner.service.vo.response.QuizAndAnswerVO;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OpenAiTextService {

    private final OpenAiService openAiService;

    @Autowired
    public OpenAiTextService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String generateSummary(String transcript) {
        CompletionRequest request = CompletionRequest.builder()
                .prompt("Summarize the following text in Korean: " + transcript)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(2000)
                .temperature(0.7)
                .build();

        return openAiService.createCompletion(request).getChoices().get(0).getText().trim();
    }

    public QuizAndAnswerVO generateQuiz(String transcript) {
        CompletionRequest request = CompletionRequest.builder()
                .prompt("Based on the following text, generate a quiz question and answer in Korean in the format 'Quiz: <question> Answer: <answer>': " + transcript)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(100)
                .temperature(0.7)
                .build();

        String response = openAiService.createCompletion(request).getChoices().get(0).getText().trim();

        // "Quiz:"와 "Answer:"로 분리
        String quiz = "";
        String answer = "";
        String[] parts = response.split("Answer:");
        if (parts.length == 2) {
            quiz = parts[0].replace("Quiz:", "").trim();
            answer = parts[1].trim();
        } else {
            // 형식이 맞지 않는 경우 예외 처리나 기본값 설정 가능
            throw new IllegalArgumentException("Response format is incorrect: " + response);
        }

        return new QuizAndAnswerVO(null, quiz, answer);
    }

}
