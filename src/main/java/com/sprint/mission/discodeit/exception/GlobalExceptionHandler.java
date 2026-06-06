package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.error("커스텀 예외 발생: code={}, message={}, details={}",
        e.getErrorCode(), e.getMessage(), e.getDetails());

    HttpStatus httpStatus = parseHttpStatus(e);

    ErrorResponse response = new ErrorResponse(
        e.getTimestamp(),
        e.getErrorCode().name(),
        e.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        httpStatus.value()
    );

    return ResponseEntity
        .status(httpStatus)
        .body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("잘못된 요청 예외 발생: message={}", e.getMessage());

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "INVALID_REQUEST",
        e.getMessage(),
        Map.of(),
        e.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
    log.error("리소스 조회 실패 예외 발생: message={}", e.getMessage());

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "RESOURCE_NOT_FOUND",
        e.getMessage(),
        Map.of(),
        e.getClass().getSimpleName(),
        HttpStatus.NOT_FOUND.value()
    );

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 예외 발생", e);

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "INTERNAL_SERVER_ERROR",
        "서버 내부 오류가 발생했습니다.",
        Map.of(),
        e.getClass().getSimpleName(),
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    );

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
  }

  private HttpStatus parseHttpStatus(DiscodeitException e) {
    ErrorCode code = e.getErrorCode();

    return switch (code) {
      case INVALID_LOGIN -> HttpStatus.UNAUTHORIZED;

      case USER_NOT_FOUND,
           USER_STATUS_NOT_FOUND,
           CHANNEL_NOT_FOUND,
           MESSAGE_NOT_FOUND,
           READ_STATUS_NOT_FOUND,
           AUTHOR_NOT_FOUND,
           FILE_NOT_FOUND -> HttpStatus.NOT_FOUND;

      case DUPLICATE_EMAIL,
           DUPLICATE_USERNAME,
           DUPLICATE_USER_STATUS,
           DUPLICATE_FILE -> HttpStatus.CONFLICT;

      case PRIVATE_CHANNEL_UPDATE -> HttpStatus.BAD_REQUEST;

      case FILE_UPLOAD_FAILED,
           FILE_DOWNLOAD_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}