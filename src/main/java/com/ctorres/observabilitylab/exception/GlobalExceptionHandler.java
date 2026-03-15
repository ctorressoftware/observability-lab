package com.ctorres.observabilitylab.exception;

import com.ctorres.observabilitylab.dto.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<Result<String>> handleRequestValidationException(RequestValidationException ex) {
        return ResponseEntity.unprocessableContent()
                .body(Result.error(CodeError.VALIDATION_ERROR.name(), ex.getMessage()));
    }

    @ExceptionHandler(ControlledErrorException.class)
    public ResponseEntity<Result<String>> handleControlledErrorException(ControlledErrorException ex) {
        return ResponseEntity.internalServerError()
                .body(Result.error(CodeError.CONTROLLED_ERROR.name(), ex.getMessage()));
    }

    @ExceptionHandler(InterruptedThreadException.class)
    public ResponseEntity<Result<String>> handleInterruptedThreadException(InterruptedThreadException ex) {
        return ResponseEntity.internalServerError()
                .body(Result.error(CodeError.INTERRUPTED_THREAD.name(), ex.getMessage()));
    }
}
