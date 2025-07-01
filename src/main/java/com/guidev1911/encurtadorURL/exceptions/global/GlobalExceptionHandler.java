package com.guidev1911.encurtadorURL.exceptions.global;

import com.guidev1911.encurtadorURL.dto.ApiErrorResponse;
import com.guidev1911.encurtadorURL.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos.");

        ApiErrorResponse error = new ApiErrorResponse(400, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getMessage())
                .findFirst()
                .orElse("Parâmetro inválido.");

        ApiErrorResponse error = new ApiErrorResponse(400, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShortCodeGenerationException.class)
    public ResponseEntity<ApiErrorResponse> handleShortCodeGenerationException(ShortCodeGenerationException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                ZonedDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                ZonedDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidUrlFormatException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidUrlException(InvalidUrlFormatException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                ZonedDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(ExpirationDateExceedsLimitException.class)
    public ResponseEntity<ApiErrorResponse> handleLimit(ExpirationDateExceedsLimitException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, Exception ex) {
        ApiErrorResponse error = new ApiErrorResponse(status.value(), ex.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralError(Exception ex) {
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno: " + ex.getMessage(), ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}