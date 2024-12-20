package com.example.choco_planner.service;

import com.example.choco_planner.controller.dto.response.QuizAndAnswerDTO;
import com.example.choco_planner.controller.dto.response.SummaryResponse;
import com.example.choco_planner.service.vo.response.QuizAndAnswerVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
    public List<QuizAndAnswerDTO> generateQuiz(String transcript) {
        ChatMessage chatMessage = new ChatMessage("user",
                "롤: 주어지는 텍스트들을 보고 퀴즈와 정답 목록을 만들어주는 사람\n" +
                        "방식: 아래 객체 형식에 맞게 한국어로 퀴즈와 정답 목록을 생성할 것\n" +
                        "객체 형식:\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"quiz\": \"퀴즈 내용\",\n" +
                        "    \"answer\": \"정답 내용\"\n" +
                        "  },\n" +
                        "  ...\n" +
                        "]\n" +
                        "주어진 텍스트:\n" + transcript
        );

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(List.of(chatMessage))
                .maxTokens(14000)
                .temperature(0.7)
                .build();

        var result = openAiService.createChatCompletion(request);

        String rawResponse = result.getChoices().get(0).getMessage().getContent();

        // Markdown 제거
        String cleanedResponse = cleanMarkdown(rawResponse);

        // JSON 파싱
        return parseQuizAndAnswer(cleanedResponse);
    }

    private String cleanMarkdown(String response) {
        if (response.startsWith("```")) {
            int start = response.indexOf("["); // 배열 시작 위치
            int end = response.lastIndexOf("]"); // 배열 종료 위치
            if (start != -1 && end != -1) {
                return response.substring(start, end + 1).trim();
            }
        }
        return response.trim(); // Markdown이 없으면 원본 반환
    }

    private List<QuizAndAnswerDTO> parseQuizAndAnswer(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 문자열을 List<QuizAndAnswerDTO>로 변환
            return objectMapper.readValue(jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, QuizAndAnswerDTO.class));
        } catch (MismatchedInputException e) {
            // 단일 객체를 리스트로 변환
            try {
                QuizAndAnswerDTO singleQuiz = objectMapper.readValue(jsonResponse, QuizAndAnswerDTO.class);
                return List.of(singleQuiz);
            } catch (Exception innerException) {
                throw new RuntimeException("QuizAndAnswerDTO 변환 실패", innerException);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("QuizAndAnswerDTO 변환 실패", e);
        }
    }

}
