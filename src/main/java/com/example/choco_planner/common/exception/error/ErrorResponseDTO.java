package com.example.choco_planner.common.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private ErrorDTO error;

    public static ErrorResponseDTO error(ErrorType errorType) {
        return new ErrorResponseDTO(
                new ErrorDTO(
                        errorType.name(),
                        errorType.getMessage(),
                        errorType.getStatus().value(),
                        null,  // bindingResults와 timestamp를 null로 설정
                        null
                )
        );
    }

    public static ErrorResponseDTO error(ErrorType errorType, String message, Map<String, Object> bindingResults) {
        return new ErrorResponseDTO(
                new ErrorDTO(
                        errorType.name(),
                        message,
                        errorType.getStatus().value(),
                        null,  // 기본적으로 timestamp는 null로 설정되며, ErrorDTO에서 현재 시간으로 설정됨
                        bindingResults
                )
        );
    }
}
