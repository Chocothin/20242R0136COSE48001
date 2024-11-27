package com.example.choco_planner.service;

import com.example.choco_planner.service.vo.response.QuizAndAnswerVO;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OpenAiTextService {

    private final OpenAiService openAiService;

    @Autowired
    public OpenAiTextService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String generateSummary(String transcript) {
        ChatMessage message = new ChatMessage("user", "롤: 주어지는 텍스트들을 보고 요약을 해주는 사람\n" +
                "                방식: 대제목, 중제목, 소제목 3단계로 나누어서 한국어로 요약할 것\n" +
                "                주어진 텍스트:\n" + transcript);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(Collections.singletonList(message))
                .maxTokens(14000)
                .temperature(0.7)
                .build();

        // 요청 보내기
        var result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent().trim();
    }

    public QuizAndAnswerVO generateQuiz(String transcript) {
//        // Chat API 메시지 작성
//        ChatMessage message = new ChatMessage("user", "Based on the following text, generate a quiz question and answer in Korean in the format 'Quiz: <question> Answer: <answer>': " + transcript);
//
//        ChatCompletionRequest request = ChatCompletionRequest.builder()
//                .messages(Collections.singletonList(message))
//                .model("gpt-4")
//                .maxTokens(100)
//                .temperature(0.7)
//                .build();
//
//        // Chat API 호출
//        ChatCompletionResponse response = openAiService.createChatCompletion(request);
//
//        String content = response.getChoices().get(0).getMessage().getContent().trim();
//
//        // "Quiz:"와 "Answer:"로 분리
//        String quiz = "";
//        String answer = "";
//        String[] parts = content.split("Answer:");
//        if (parts.length == 2) {
//            quiz = parts[0].replace("Quiz:", "").trim();
//            answer = parts[1].trim();
//        } else {
//            throw new IllegalArgumentException("Response format is incorrect: " + content);
//        }

        return new QuizAndAnswerVO(null, "quiz", "answer");
    }
}
