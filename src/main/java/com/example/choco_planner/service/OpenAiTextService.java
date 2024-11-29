package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.SummaryResponse;
import com.example.choco_planner.service.vo.response.QuizAndAnswerVO;
import com.fasterxml.jackson.databind.ObjectMapper;
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



    public SummaryResponse generateSummary(String transcript) {
        // OpenAI 요청 생성
        ChatMessage message = new ChatMessage("user",
                "롤: 주어지는 텍스트들을 보고 요약을 해주는 사람\n" +
                        "방식: 아래 객체 형식에 맞게 한국어로 요약할 것\n" +
                        "객체 형식:\n" +
                        "{\n" +
                        "  \"response\": [\n" +
                        "    {\n" +
                        "      \"title\": \"대제목\",\n" +
                        "      \"sections\": [\n" +
                        "        {\n" +
                        "          \"subtitle\": \"중제목\",\n" +
                        "          \"contents\": [\"소제목1\", \"소제목2\", ...]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n" +
                        "주어진 텍스트:\n" + transcript
        );

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(Collections.singletonList(message))
                .maxTokens(14000)
                .temperature(0.7)
                .build();

        // OpenAI 요청 보내기
        var result = openAiService.createChatCompletion(request);

        // 응답 데이터 가져오기
        String rawResponse = result.getChoices().get(0).getMessage().getContent().trim();

        // 응답에서 Markdown 코드 블록 제거
        String cleanedResponse = removeMarkdownCodeBlock(rawResponse);

        // JSON을 객체로 매핑
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(cleanedResponse, SummaryResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 매핑 실패: " + cleanedResponse);
        }
    }

    private String removeMarkdownCodeBlock(String response) {
        // Markdown 코드 블록 제거
        if (response.startsWith("```json")) {
            return response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1).trim();
        }
        return response;
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
