package com.rvinproject.camerarentalbe.exception;

import com.rvinproject.camerarentalbe.helper.JSONFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowed() {
        return ResponseEntity.status(405)
                .body(JSONFormat.error(null, "Method tidak diizinkan"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException() {
        return ResponseEntity.internalServerError()
                .body(JSONFormat.error(null, "Terjadi kesalahan server"));
    }
}