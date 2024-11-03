package com.example.choco_planner.service;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class ClientSession {
    private final ByteArrayOutputStream accumulatedAudio;
    @Setter
    private Instant startTime;
    @Setter
    private int processedBytes;
    private final Lock lock = new ReentrantLock();

    public ClientSession() {
        this.accumulatedAudio = new ByteArrayOutputStream();
        this.startTime = null;
        this.processedBytes = 0;
    }

    public void resetAccumulatedAudio() {
        this.accumulatedAudio.reset();
        this.startTime = null;
        this.processedBytes = 0;
    }
}
