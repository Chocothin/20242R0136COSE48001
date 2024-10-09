package com.example.choco_planner.service;

import com.example.choco_planner.configuration.OpenAiConfig;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sound.sampled.*;
import java.io.*;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SpeechToTextService {

    private final OpenAiConfig openAiConfig;
    private final OpenAiAudioApi openAiAudioApi;

    private final ConcurrentHashMap<String, ByteArrayOutputStream> clientAudioMap = new ConcurrentHashMap<>();
    private final AudioFormat format = new AudioFormat(16000, 16, 1, true, false); // 16kHz, 16-bit, mono, little endian

    public SpeechToTextService(OpenAiConfig openAiConfig, OpenAiAudioApi openAiAudioApi) {
        this.openAiConfig = openAiConfig;
        this.openAiAudioApi = openAiAudioApi;
    }

    /**
     * 오디오 데이터를 누적하고, 일정 크기 이상일 경우 처리합니다.
     *
     * @param clientId        클라이언트 식별자
     * @param base64AudioData Base64 인코딩된 오디오 데이터
     * @return 전사된 텍스트 또는 상태 메시지
     */
    public synchronized String processAudioChunk(String clientId, String base64AudioData, String className, String classContent) {
        try {
            // Base64 디코딩
            byte[] audioBytes = Base64.getDecoder().decode(base64AudioData);
            System.out.println("디코딩된 오디오 데이터 크기: " + audioBytes.length + " 바이트");

            // 클라이언트별 오디오 누적
            ByteArrayOutputStream accumulatedAudio = clientAudioMap.computeIfAbsent(clientId, k -> new ByteArrayOutputStream());
            accumulatedAudio.write(audioBytes);

            // 누적된 오디오 데이터가 충분한지 확인
            int bytesPerSecond = (int) (format.getFrameSize() * format.getFrameRate());
            if (accumulatedAudio.size() >= bytesPerSecond * 10) {
                byte[] fullAudioBytes = accumulatedAudio.toByteArray();
                accumulatedAudio.reset(); // 누적 데이터 초기화

                // WAV 변환
                byte[] wavData = convertToWav(fullAudioBytes, format);

                // WAV 데이터를 리소스로 감싸서 API로 전송
                Resource audioResource = new ByteArrayResource(wavData) {
                    @Override
                    public String getFilename() {
                        return "audio.wav";
                    }
                };

                OpenAiAudioTranscriptionModel transcriptionModel = openAiConfig.createCustomModel(openAiAudioApi, className, classContent);

                // OpenAI API 호출을 통해 전사된 텍스트 가져오기
                String transcription = transcriptionModel.call(new AudioTranscriptionPrompt(audioResource)).getResult().getOutput();
                System.out.println("전사 결과: " + transcription);

                return transcription;
            }

            return "";
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
            return "오디오 처리 중 오류 발생: " + e.getMessage();
        }
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
            System.err.println("WAV 변환 중 오류 발생: " + e.getMessage());
            throw new IOException("WAV 변환에 실패했습니다.", e);
        }
    }

//    /**
//     * MultipartFile을 사용하여 오디오 파일을 전사합니다.
//     *
//     * @param file 업로드된 오디오 파일
//     * @return 전사된 텍스트
//     * @throws IOException
//     */
//    public String transcribe(MultipartFile file) throws IOException {
//        byte[] audioBytes = file.getBytes();
//        System.out.println("파일 크기: " + audioBytes.length + " 바이트");
//
//        // 바이트 배열을 리소스로 감싸서 API로 전송
//        Resource audioResource = new ByteArrayResource(audioBytes) {
//            @Override
//            public String getFilename() {
//                return file.getOriginalFilename(); // 파일 이름 유지
//            }
//        };
//
//        // OpenAI API 호출
//        AudioTranscriptionResponse response = transcriptionModel.call(new AudioTranscriptionPrompt(audioResource));
//        return response.getResult().getOutput();
//    }
//
//    /**
//     * 녹음 세션을 종료하고 누적된 데이터를 제거합니다.
//     *
//     * @param clientId 클라이언트 식별자
//     */
//    public void endSession(String clientId) {
//        clientAudioMap.remove(clientId);
//    }
}
