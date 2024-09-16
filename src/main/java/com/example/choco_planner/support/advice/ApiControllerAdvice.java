package com.example.choco_planner.support.advice;

import com.example.choco_planner.common.exception.HandleException;
import com.example.choco_planner.common.exception.error.ErrorResponseDTO;
import com.example.choco_planner.common.exception.error.ErrorType;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class ApiControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(ApiControllerAdvice.class);

    @ExceptionHandler(HandleException.class)
    public ResponseEntity<ErrorResponseDTO> adviceHandleException(HandleException exception) {
        String message = defaultMessageIfHasNotExceptionMessage(exception, exception.getErrorType());

        log.error("Exception : {}, message: {}, bindingResults: {}",
                exception.getErrorType(),
                message,
                exception.getBindingResults());

        return ResponseEntity.status(exception.getErrorType().getStatus())
                .body(ErrorResponseDTO.error(exception.getErrorType()));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            UnexpectedTypeException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestPartException.class,
            IllegalArgumentException.class
    })

    public ResponseEntity<ErrorResponseDTO> adviceFrameworkException(Exception exception) {
        String message = defaultMessageIfHasNotExceptionMessage(exception, ErrorType.INVALID_REQUEST);

        log.error("[INTERCEPTOR ERROR][EXCEPTION CLASS: {}]", exception.getClass().getSimpleName());
        log.error("Exception : {}", message, exception);

        return ResponseEntity.badRequest().body(ErrorResponseDTO.error(ErrorType.INVALID_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> exception(Exception exception) {
        log.error("Exception : {}", exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body(ErrorResponseDTO.error(ErrorType.COMMON_ERROR));
    }

    private String defaultMessageIfHasNotExceptionMessage(Exception exception, ErrorType errorType) {
        return exception.getMessage() != null ? exception.getMessage() : errorType.getMessage();
    }
}
