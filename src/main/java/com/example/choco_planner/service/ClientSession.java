package com.example.choco_planner.service;

import com.example.choco_planner.storage.entity.RecordingEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class ClientSession {
    private final ByteArrayOutputStream accumulatedAudio; // 누적된 오디오 데이터
    @Setter
    private Instant startTime; // 녹음 시작 시간
    @Setter
    private int processedBytes; // 처리된 바이트 수
    private final Lock lock = new ReentrantLock(); // 스레드 안전성 확보를 위한 Lock

    @Setter
    private RecordingEntity recording; // 현재 세션의 RecordingEntity

    public ClientSession() {
        this.accumulatedAudio = new ByteArrayOutputStream();
        this.startTime = null;
        this.processedBytes = 0;
        this.recording = null; // 초기에는 null로 설정
    }

    /**
     * 세션 상태를 초기화합니다.
     */
    public void resetAccumulatedAudio() {
        this.accumulatedAudio.reset();
        this.startTime = null;
        this.processedBytes = 0;
        this.recording = null; // RecordingEntity도 초기화
    }
}
