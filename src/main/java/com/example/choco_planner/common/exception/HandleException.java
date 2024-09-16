package com.example.choco_planner.common.exception;

import com.example.choco_planner.common.exception.error.ErrorType;

import java.util.Collections;
import java.util.Map;
import lombok.Getter;

@Getter
public class HandleException extends RuntimeException {
    private final ErrorType errorType;
    private final Map<String, String> bindingResults;

    // Constructor with errorType and default bindingResults
    public HandleException(ErrorType error) {
        super(error.getMessage());
        this.errorType = error;
        this.bindingResults = Collections.emptyMap();  // 기본값을 빈 맵으로 설정
    }

    // Constructor with errorType and bindingResults
    public HandleException(ErrorType error, Map<String, String> bindingResults) {
        super(error.getMessage());
        this.errorType = error;
        this.bindingResults = bindingResults != null ? bindingResults : Collections.emptyMap();
    }

    // Constructor using custom message
    public HandleException(ErrorType error, String message) {
        super(message);
        this.errorType = error;
        this.bindingResults = Collections.emptyMap();  // 기본값을 빈 맵으로 설정
    }

    // Constructor with custom message and bindingResults
    public HandleException(ErrorType error, String message, Map<String, String> bindingResults) {
        super(message);
        this.errorType = error;
        this.bindingResults = bindingResults != null ? bindingResults : Collections.emptyMap();
    }
}
