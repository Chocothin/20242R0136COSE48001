package com.example.choco_planner.service;

import com.example.choco_planner.storage.entity.RecordingEntity;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.*;
import org.java_websocket.client.WebSocketClient;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealTimeSTTService {

    private final ConcurrentHashMap<String, WebSocketClient> clientSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BlockingQueue<String>> messageQueues = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ByteArrayOutputStream> audioBuffers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> classIds = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> userIds = new ConcurrentHashMap<>();
    private final WebSocketClientImpl webSocketClientImpl;
    private final RecordingService recordingService;
    private final RecordingDetailService recordingDetailService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    /**
     * 특정 클라이언트 ID에 대해 WebSocket 세션이 존재하는지 확인합니다.
     *
     * @param clientId 클라이언트 ID (세션 ID)
     * @return 세션 존재 여부
     */
    public boolean hasSession(String clientId) {
        return clientSessionMap.containsKey(clientId);
    }

    public void startSession(String clientId, Long classId, Long userId) {
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
        WebSocketClient session = webSocketClientImpl.connect(clientId, messageQueue);

        clientSessionMap.put(clientId, session);
        messageQueues.put(clientId, messageQueue);
        audioBuffers.put(clientId, new ByteArrayOutputStream());
        classIds.put(clientId, classId);
        userIds.put(clientId, userId);

        log.info("[{}] 세션이 시작되었습니다.", clientId);

        // 3초마다 GPT 요청 처리
        scheduler.scheduleAtFixedRate(() -> processBufferedAudio(clientId), 3, 10, TimeUnit.SECONDS);
    }

    public void bufferAudioChunk(String clientId, String base64AudioData, Long classId, Long userId) {
        ByteArrayOutputStream buffer = audioBuffers.get(clientId);
        if (buffer == null) {
            throw new IllegalArgumentException("세션이 존재하지 않습니다: " + clientId);
        }

        // 세션에 ID 업데이트
        classIds.put(clientId, classId);
        userIds.put(clientId, userId);

        try {
            byte[] audioBytes = Base64.getDecoder().decode(base64AudioData);
            buffer.write(audioBytes);
            log.info("[{}] 오디오 데이터를 버퍼에 추가했습니다. 현재 크기: {} bytes", clientId, buffer.size());
        } catch (Exception e) {
            log.error("[{}] 오디오 데이터를 버퍼에 추가 중 오류 발생: {}", clientId, e.getMessage());
        }
    }

    private void processBufferedAudio(String clientId) {
        ByteArrayOutputStream buffer = audioBuffers.get(clientId);
        WebSocketClient session = clientSessionMap.get(clientId);
        BlockingQueue<String> messageQueue = messageQueues.get(clientId);

        if (buffer == null || session == null || messageQueue == null) {
            log.warn("[{}] 처리할 세션이 없습니다.", clientId);
            return;
        }

        try {
            if (buffer.size() == 0) {
                log.info("[{}] 버퍼에 데이터가 없습니다. 처리 생략.", clientId);
                return;
            }

            // 버퍼 데이터 전송
            byte[] audioData = buffer.toByteArray();
            buffer.reset(); // 버퍼 초기화

            ByteArrayResource audioResource = new ByteArrayResource(audioData) {
                @Override
                public String getFilename() {
                    return "audio.wav";
                }
            };

            // GPT 전송 및 전사 결과
            String transcriptionResult = webSocketClientImpl.sendAudioData(session, audioResource, messageQueue);

            log.info("[{}] 전사 결과: {}", clientId, transcriptionResult);

            // 녹음 및 세부 데이터 저장
            Long classId = classIds.get(clientId);
            Long userId = userIds.get(clientId);

            if (classId == null || userId == null) {
                log.warn("[{}] classId 또는 userId가 설정되지 않았습니다.", clientId);
                return;
            }

            RecordingEntity recording = recordingService.saveRecording(userId, classId);
            recordingDetailService.saveRecordingDetail(recording, transcriptionResult);

            // 클라이언트로 전송

        } catch (Exception e) {
            log.error("[{}] 버퍼 처리 중 오류 발생: {}", clientId, e.getMessage());
        }
    }

    public void endSession(String clientId) {
        WebSocketClient session = clientSessionMap.remove(clientId);
        BlockingQueue<String> messageQueue = messageQueues.remove(clientId);
        audioBuffers.remove(clientId);
        classIds.remove(clientId);
        userIds.remove(clientId);

        if (session != null) {
            webSocketClientImpl.disconnect(session);
            log.info("[{}] 세션이 종료되었습니다.", clientId);
        }

        if (messageQueue != null) {
            messageQueue.clear();
            log.info("[{}] 메시지 큐가 정리되었습니다.", clientId);
        }
    }

    @PreDestroy
    public void shutdown() {
        clientSessionMap.forEach((clientId, session) -> {
            webSocketClientImpl.disconnect(session);
            log.info("[{}] 세션이 종료되었습니다.", clientId);
        });

        messageQueues.forEach((clientId, queue) -> {
            queue.clear();
            log.info("[{}] 메시지 큐가 정리되었습니다.", clientId);
        });

        scheduler.shutdown();
        log.info("모든 WebSocket 세션이 종료되고, 스케줄러가 중지되었습니다.");
    }
}
