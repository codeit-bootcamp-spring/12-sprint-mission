package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.auth.AccessDeniedAuthException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
    log.warn("IllegalArgumentException : {}", e.getMessage());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(e, 400));
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    int status = e.getErrorCode().getStatus();

    if (status >= 500) {
      log.error("{}: message={}, details={}",
          e.getClass().getSimpleName(),
          e.getMessage(),
          e.getDetails(),
          e);
    } else {
      log.warn("{}: message={}, details={}",
          e.getClass().getSimpleName(),
          e.getMessage(),
          e.getDetails());
    }

    return ResponseEntity
        .status(HttpStatus.valueOf(status))
        .body(new ErrorResponse(e, status)
        );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e) {
    log.warn("요청 유효성 검사 실패 : {}", e.getMessage());

    Map<String, Object> validationErrors = new LinkedHashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMeString = error.getDefaultMessage();
      validationErrors.put(fieldName, errorMeString);
    });

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "VALIDATION_ERROR",
        "요청 데이터 유효성 검사에 실패하였습니다.",
        validationErrors,
        e.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException e) {
    return accessDeniedResponse(e);
  }

  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
      org.springframework.security.access.AccessDeniedException e) {
    return accessDeniedResponse(e);
  }

  private ResponseEntity<ErrorResponse> accessDeniedResponse(Exception e) {
    AccessDeniedAuthException exception = new AccessDeniedAuthException();
    int status = exception.getErrorCode().getStatus();

    log.warn("{}: message={}", e.getClass().getSimpleName(), e.getMessage());

    return ResponseEntity
        .status(HttpStatus.valueOf(status))
        .body(new ErrorResponse(exception, status));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAll(Exception e) {
    log.error("Internal Server Error", e);

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "INTERNAL_SERVER_ERROR",
        "내부 서버 오류가 발생했습니다",
        Map.of(),
        "Internal Server Error",
        HttpStatus.INTERNAL_SERVER_ERROR.value()//500
    );

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
  }
}
