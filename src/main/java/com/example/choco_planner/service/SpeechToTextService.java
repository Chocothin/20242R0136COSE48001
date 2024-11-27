package com.example.choco_planner.service;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import com.example.choco_planner.common.domain.CustomUser;
import com.example.choco_planner.configuration.OpenAiConfig;
import com.example.choco_planner.storage.entity.RecordingEntity;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpeechToTextService {

    private final OpenAiConfig openAiConfig;
    private final OpenAiAudioApi openAiAudioApi;
    private final RecordingService recordingService;
    private final RecordingDetailService recordingDetailService;

    // 클라이언트별 세션 관리를 위한 맵
    private final ConcurrentHashMap<String, ClientSession> clientSessionMap = new ConcurrentHashMap<>();

    // 최소 청크 크기 (예: 32,000 바이트 = 1초 분량의 오디오 데이터)
    private final int MIN_CHUNK_SIZE = 16000 * 2 * 1 * 10; // 32,000 bytes
    // 최대 청크 크기 (10초 분량)
    private final int MAX_CHUNK_SIZE = 16000 * 2 * 1 * 20; // 320,000 bytes (10초)

    // 오디오 포맷 설정: 16kHz, 16-bit, mono, little endian
    private final AudioFormat format = new AudioFormat(16000f, 16, 1, true, false);

    // ExecutorService for asynchronous API calls
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // 적절한 스레드 수로 조정

    /**
     * 오디오 데이터를 누적하고, 침묵 구간을 기준으로 청크를 분리하거나 청크 크기 제한 시 API를 호출합니다.
     *
     * @param clientId        클라이언트 식별자
     * @param base64AudioData Base64 인코딩된 오디오 데이터
     * @return 전사된 텍스트 또는 상태 메시지
     */
    // 기존 코드 일부 생략


    public String processAudioChunk(
            String clientId,
            String base64AudioData,
            Long classId,
            Long userId
    ) {
//        RecordingEntity recording = recordingService.saveRecording(userId, classId);

        ClientSession session = clientSessionMap.computeIfAbsent(clientId, k -> new ClientSession());
        session.getLock().lock();

        try {
            final RecordingEntity recording; // recording을 final로 선언
            if (session.getRecording() == null) {
                recording = recordingService.saveRecording(userId, classId);
                session.setRecording(recording);
            } else {
                recording = session.getRecording();
            }
            recordingService.updateStoppedAt(recording.getId());

            byte[] audioBytes = Base64.getDecoder().decode(base64AudioData);
            session.getAccumulatedAudio().write(audioBytes);

            byte[] accumulatedData = session.getAccumulatedAudio().toByteArray();
            int processedBytes = session.getProcessedBytes();
            byte[] newData = Arrays.copyOfRange(accumulatedData, processedBytes, accumulatedData.length);

            if (newData.length < MIN_CHUNK_SIZE) {
                return ""; // 최소 분량 이하일 경우 추가 축적 필요
            }

            StringBuilder transcriptionResult = new StringBuilder();
            List<byte[]> audioChunks = splitAudioOnSilence(newData, format);

            for (byte[] chunk : audioChunks) {
                if (chunk.length < MIN_CHUNK_SIZE) {
                    continue; // 최소 분량 미달 청크는 무시
                }

                CompletableFuture<String> transcriptionFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        byte[] wavData = convertToWav(chunk, format);
                        Resource audioResource = new ByteArrayResource(wavData) {
                            @Override
                            public String getFilename() {
                                return "audio.wav";
                            }
                        };
                        OpenAiAudioTranscriptionModel transcriptionModel = openAiConfig.createCustomModel(openAiAudioApi);
                        String result =  transcriptionModel.call(new AudioTranscriptionPrompt(audioResource)).getResult().getOutput();
                        recordingDetailService.saveRecordingDetail(
                                recording, result
                        );
                        return result;
                    } catch (Exception e) {
                        log.error("[{}] 전사 중 오류 발생: {}", clientId, e.getMessage());
                        return "전사 중 오류 발생: " + e.getMessage();
                    }
                }, executorService);

                transcriptionResult.append(transcriptionFuture.get()).append(" ");
                processedBytes += chunk.length;
            }

            session.setProcessedBytes(processedBytes);

            if (accumulatedData.length >= MAX_CHUNK_SIZE) {
                byte[] remainingData = Arrays.copyOfRange(accumulatedData, processedBytes, accumulatedData.length);
                if (remainingData.length > 0) {
                    byte[] wavData = convertToWav(remainingData, format);
                    CompletableFuture<String> transcriptionFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            Resource audioResource = new ByteArrayResource(wavData) {
                                @Override
                                public String getFilename() {
                                    return "audio.wav";
                                }
                            };
                            OpenAiAudioTranscriptionModel transcriptionModel = openAiConfig.createCustomModel(openAiAudioApi);
                            String result =  transcriptionModel.call(new AudioTranscriptionPrompt(audioResource)).getResult().getOutput();
                            recordingDetailService.saveRecordingDetail(
                                    recording, result
                            );
                            return result;
                        } catch (Exception e) {
                            log.error("[{}] 전사 중 오류 발생 (최대 축적 도달): {}", clientId, e.getMessage());
                            return "전사 중 오류 발생: " + e.getMessage();
                        }
                    }, executorService);

                    transcriptionResult.append(transcriptionFuture.get()).append(" ");
                    processedBytes = accumulatedData.length;
                    session.setProcessedBytes(processedBytes);
                    session.getAccumulatedAudio().reset();
                }
            }

            if (processedBytes > accumulatedData.length) {
                log.warn("[{}] 처리된 바이트 수({})가 누적된 데이터 크기({})를 초과했습니다. 클라이언트 세션을 리셋합니다.", clientId, processedBytes, accumulatedData.length);
                session.resetAccumulatedAudio();
                processedBytes = 0;
            } else {
                byte[] updatedAccumulated = Arrays.copyOfRange(accumulatedData, processedBytes, accumulatedData.length);
                session.getAccumulatedAudio().reset();
                session.getAccumulatedAudio().write(updatedAccumulated);
                session.setProcessedBytes(0);
            }


            return transcriptionResult.toString().trim();

        } catch (Exception e) {
            log.error("[{}] 오디오 처리 중 오류 발생: {}", clientId, e.getMessage());
            return "오디오 처리 중 오류 발생: " + e.getMessage();
        } finally {
            session.getLock().unlock();
        }
    }



    /**
     * 오디오 데이터에서 침묵 구간을 감지하여 청크를 분리합니다.
     *
     * @param audioBytes 오디오 데이터 바이트 배열
     * @param format     오디오 포맷
     * @return 침묵을 기준으로 분리된 오디오 청크 리스트
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    private List<byte[]> splitAudioOnSilence(byte[] audioBytes, AudioFormat format) throws UnsupportedAudioFileException, IOException {
        List<byte[]> audioChunks = new ArrayList<>();
        ByteArrayOutputStream currentChunk = new ByteArrayOutputStream();

        // 원시 PCM 데이터로 AudioInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioBytes);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioBytes.length / format.getFrameSize());

        // TarsosDSP용 AudioInputStream 래핑
        JVMAudioInputStream tarsosAudioInputStream = new JVMAudioInputStream(audioInputStream);

        // AudioDispatcher 생성: 버퍼 크기 1024, 오버랩 512
        AudioDispatcher dispatcher = new AudioDispatcher(tarsosAudioInputStream, 1024, 512);

        // SilenceDetector 설정: -60 dB 이하를 침묵으로 간주 (임계값 조정)
        double silenceThreshold = -60.0; // dB
        SilenceDetector silenceDetector = new SilenceDetector(silenceThreshold, false);
        dispatcher.addAudioProcessor(silenceDetector);

        // 침묵 감지 및 청크 분리 프로세서 추가
        dispatcher.addAudioProcessor(new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {
                double currentSPL = silenceDetector.currentSPL();
                if (currentSPL < silenceThreshold) { // 침묵 감지
                    if (currentChunk.size() > 0) {
                        audioChunks.add(currentChunk.toByteArray());
                        currentChunk.reset();
                    }
                } else {
                    try {
                        currentChunk.write(audioEvent.getByteBuffer());
                    } catch (IOException e) {
                        log.error("청크 작성 중 오류 발생: {}", e.getMessage());
                        // 예외 발생 시 프로세싱을 중단
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void processingFinished() {
                if (currentChunk.size() > 0) {
                    audioChunks.add(currentChunk.toByteArray());
                }
            }
        });

        // 디스패처 실행
        dispatcher.run();

        log.info("청크 분리 완료: {} 개의 청크 생성됨", audioChunks.size());
        return audioChunks;
    }

    /**
     * 바이트 배열을 WAV 형식으로 변환합니다.
     *
     * @param audioBytes 오디오 데이터
     * @param format     오디오 포맷
     * @return WAV 형식의 바이트 배열
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    private byte[] convertToWav(byte[] audioBytes, AudioFormat format) throws IOException, UnsupportedAudioFileException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
             AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioBytes.length / format.getFrameSize());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("WAV 변환 중 오류 발생: {}", e.getMessage());
            throw new IOException("WAV 변환에 실패했습니다.", e);
        }
    }

    /**
     * 녹음 세션을 종료하고 누적된 데이터를 제거합니다.
     *
     * @param clientId 클라이언트 식별자
     */
    public void endSession(String clientId) {
        clientSessionMap.remove(clientId);
        log.info("[{}] 세션 종료 및 데이터 제거", clientId);
    }

    /**
     * ExecutorService를 종료합니다. (애플리케이션 종료 시 호출)
     */
    @PreDestroy
    public void shutdownExecutorService() {
        log.info("ExecutorService 종료 중...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("ExecutorService가 60초 내에 종료되지 않았습니다. 즉시 종료 시도 중...");
                executorService.shutdownNow();
            }
            log.info("ExecutorService가 성공적으로 종료되었습니다.");
        } catch (InterruptedException e) {
            log.error("ExecutorService 종료 중 인터럽트 발생: {}", e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
