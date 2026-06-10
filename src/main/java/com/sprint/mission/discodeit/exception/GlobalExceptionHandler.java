package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 공통 예외 처리부
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 오류 발생 : {}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(e, 500);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // 커스텀 에러 처리
  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(DiscodeitException e) {
    log.error("커스텀 예외 발생 : code={}, message={}, detail={}", e.getErrorCode(), e.getMessage(),
        e.getDetails());
    HttpStatus httpStatus = parseHttpStatus(e);
    ErrorResponse errorResponse = new ErrorResponse(e, httpStatus.value());
    return ResponseEntity.status(httpStatus).body(errorResponse);
  }

  // 도메인 예외를 HttpStatus 번호로 매핑하는 코드
  private HttpStatus parseHttpStatus(DiscodeitException e) {
    ErrorCode code = e.getErrorCode();
    return switch (code) {
      case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND, READ_STATUS_NOT_FOUND,
           USER_STATUS_NOT_FOUND, BINARY_CONTENT_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case USER_DUPLICATE, USER_STATUS_DUPLICATE, READ_STATUS_DUPLICATE -> HttpStatus.CONFLICT;
      case INVALID_USER_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
      case INVALID_REQUEST, PRIVATE_CHANNEL_UPDATE -> HttpStatus.BAD_REQUEST;
      case CHANNEL_ACCESS_DENIED -> HttpStatus.FORBIDDEN;
//      case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }

  // 유효성 검사 예외처리부
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e) {
    log.error("요청 유효성 검사 실패 : {}", e.getMessage());

    Map<String, Object> validationErrors = new LinkedHashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      validationErrors.put(fieldName, errorMessage);
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
}
