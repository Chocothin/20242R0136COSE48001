package com.example.choco_planner.configuration;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class OpenAiConfig{
    private final String CUSTOM_PROMPT_TEMPLATE = "Please provide an accurate transcription for this audio file. The class is titled '%s', and the key topics covered include: %s. Ensure the transcription reflects any technical terms or important details mentioned.";
    private final String apiKey;

    // 생성자 주입을 사용하여 @Value 값을 가져옴
    public OpenAiConfig(@Value("${spring.ai.openai.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Bean
    public OpenAiAudioApi openAiAudioApi() {
        // API Key를 환경 변수에서 가져옴
        return new OpenAiAudioApi(apiKey);
    }

    @Bean
    public OpenAiAudioTranscriptionModel transcriptionModel(OpenAiAudioApi openAiAudioApi) {
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .withLanguage("ko")
                .withPrompt("Create transcription for this audio file.")
                .withTemperature(0f)
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .withModel("whisper-1")
                .build();
        return new OpenAiAudioTranscriptionModel(openAiAudioApi, options);
    }

    public OpenAiAudioTranscriptionModel createCustomModel(OpenAiAudioApi openAiAudioApi, String className, String ClassContext) {
        String customPrompt = String.format(
                CUSTOM_PROMPT_TEMPLATE,
                className,
                ClassContext);

        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .withLanguage("ko")
                .withPrompt(customPrompt)
                .withTemperature(0f)
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .withModel("whisper-1")
                .build();
        return new OpenAiAudioTranscriptionModel(openAiAudioApi, options);
    }
}
