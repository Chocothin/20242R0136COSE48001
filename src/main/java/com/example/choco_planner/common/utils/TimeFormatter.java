package com.example.choco_planner.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeFormatter {

    /**
     * 주어진 초 단위 시간을 mm:ss 형식으로 변환합니다.
     *
     * @param seconds 초 단위 시간
     * @return mm:ss 형식의 문자열
     */
    public static String formatToMinutesAndSeconds(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * LocalDateTime을 기준 시점과 비교하여 mm:ss 형식으로 반환합니다.
     *
     * @param recordedAt 시작 시점
     * @param referenceTime 기준 시점
     * @return mm:ss 형식의 문자열
     */
    public static String formatRecordingTimestamp(LocalDateTime recordedAt, LocalDateTime referenceTime) {
        Duration duration = Duration.between(referenceTime, recordedAt);
        return formatToMinutesAndSeconds(duration.getSeconds());
    }
}
