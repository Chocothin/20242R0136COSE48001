package com.example.choco_planner.common.exception.error;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {
    private String code;
    private String message;
    private int status;
    private LocalDateTime timestamp = LocalDateTime.now();  // 기본값으로 현재 시간 설정
    private Map<String, Object> bindingResults;
}
