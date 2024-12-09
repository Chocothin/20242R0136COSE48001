package com.example.choco_planner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
@Component
@Slf4j
public class WebSocketClientImpl {

    private static final String REALTIME_API_URL = "wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01";
    @Value("${spring.ai.openai.api-key}")
    private String API_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketClient connect(String clientId, BlockingQueue<String> messageQueue) {
        try {
            URI uri = new URI(REALTIME_API_URL);

            WebSocketClient client = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    log.info("[{}] WebSocket 연결 성공", clientId);
                }

                @Override
                public void onMessage(String message) {
                    log.info("[{}] 메시지 수신: {}", clientId, message);
                    if (messageQueue != null) {
                        messageQueue.offer(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("[{}] WebSocket 연결 종료: {} - {}", clientId, code, reason);
                }

                @Override
                public void onError(Exception ex) {
                    log.error("[{}] WebSocket 오류 발생: {}", clientId, ex.getMessage());
                }
            };

            client.addHeader("Authorization", "Bearer " + API_KEY);
            client.addHeader("OpenAI-Beta", "realtime=v1");

            client.connectBlocking();
            return client;
        } catch (Exception e) {
            throw new RuntimeException("WebSocket 연결 중 오류 발생", e);
        }
    }

    public String sendAudioData(WebSocketClient session, Resource audioResource, BlockingQueue<String> messageQueue) {
        try {
            // 오디오 데이터를 Base64로 인코딩
            byte[] audioBytes = audioResource.getInputStream().readAllBytes();
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

            // 전송할 이벤트 메시지 생성 (API 사양에 맞게 수정)
            ObjectMapper objectMapper = new ObjectMapper();
            String eventMessage = objectMapper.writeValueAsString(new WebSocketEvent(
                    "conversation.item.create",
                    new AudioItem(
                                "message",
                                "user",
                                new AudioContent[] {
                                    new AudioContent("input_audio", base64Audio)
                                }
                            )
            ));

            // 메시지 전송
            session.send(eventMessage);

            // response.create 이벤트 전송 (필수로 전송해야 함)
            String responseCreateEvent = objectMapper.writeValueAsString(new ResponseCreateEvent("response.create"));
            session.send(responseCreateEvent);

            // 메시지 수신 대기
            String response = messageQueue.poll(10, TimeUnit.SECONDS);
            if (response == null) {
                throw new RuntimeException("응답 시간 초과");
            }

            // 응답 JSON 파싱하여 전사된 텍스트 추출
            String transcriptionResult = extractTranscriptionFromResponse(response);
            System.out.println("전사 결과: " + transcriptionResult);
            return transcriptionResult;
        } catch (Exception e) {
            throw new RuntimeException("오디오 데이터 전송 중 오류 발생", e);
        }
    }

    // 응답 메시지에서 전사된 텍스트 추출
    private String extractTranscriptionFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);

            // JSON에서 전사 결과 추출
            if (jsonResponse.has("type") && "conversation.item.created".equals(jsonResponse.get("type").asText())) {
                JsonNode item = jsonResponse.get("item");
                if (item != null && item.has("content")) {
                    JsonNode contentArray = item.get("content");
                    for (JsonNode content : contentArray) {
                        if ("output_text".equals(content.get("type").asText())) {
                            return content.get("text").asText();
                        }
                    }
                }
            }

            // 기본 반환값 (내용이 없을 경우)
            return "";
        } catch (Exception e) {
            throw new RuntimeException("응답 메시지 파싱 중 오류 발생", e);
        }
    }

    // WebSocket 이벤트 메시지 구조 (사양에 맞게 수정)
    private record WebSocketEvent(String type, AudioItem item) {}
    private record AudioItem(String type, String role, AudioContent[] content) {}
    private record AudioContent(String type, String audio) {}
    private record ResponseCreateEvent(String type) {}

    public void disconnect(WebSocketClient session) {
        try {
            session.closeBlocking();
        } catch (Exception e) {
            throw new RuntimeException("WebSocket 세션 종료 중 오류 발생", e);
        }
    }
}
