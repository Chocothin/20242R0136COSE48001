package com.example.choco_planner.common.exception.error;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "Member not found."),
    COMMON_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during processing."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
