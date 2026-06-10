package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.error("DiscodeitException 발생: code={}, message={}", e.getErrorCode().name(),
        e.getMessage());
    int status = resolveStatus(e);
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(e, status));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    Map<String, Object> fieldErrors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      fieldErrors.put(fieldName, errorMessage);
    });

    log.warn("유효성 검사 실패: {}", fieldErrors);

    int status = HttpStatus.BAD_REQUEST.value();
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        ErrorCode.INVALID_REQUEST.name(),
        ErrorCode.INVALID_REQUEST.getMessage(),
        fieldErrors,
        e.getClass().getSimpleName(),
        status
    );
    return ResponseEntity.status(status).body(errorResponse);
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예외 발생: type={}, message={}", e.getClass().getSimpleName(), e.getMessage(), e);
    int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(e, status));
  }

  private int resolveStatus(DiscodeitException e) {
    return switch (e.getErrorCode()) {
      case USER_NOT_FOUND,
           CHANNEL_NOT_FOUND,
           MESSAGE_NOT_FOUND,
           READ_STATUS_NOT_FOUND,
           USER_STATUS_NOT_FOUND,
           BINARY_CONTENT_NOT_FOUND -> HttpStatus.NOT_FOUND.value();
      case DUPLICATE_USER,
           DUPLICATE_READ_STATUS,
           DUPLICATE_USER_STATUS -> HttpStatus.CONFLICT.value();
      case PRIVATE_CHANNEL_UPDATE,
           INVALID_USER_CREDENTIALS,
           INVALID_REQUEST -> HttpStatus.BAD_REQUEST.value();
      default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
    };
  }
}
