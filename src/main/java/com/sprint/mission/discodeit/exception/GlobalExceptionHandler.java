package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 404: Not Found
  @ExceptionHandler({
      UserNotFoundException.class,
      ChannelNotFoundException.class,
      MessageNotFoundException.class,
      BinaryContentNotFoundException.class,
      ReadStatusNotFoundException.class,
      UserStatusNotFoundException.class
  })
  public ResponseEntity<ErrorResponse> handleNotFound(DiscodeitException e) {
    log.warn("[{}] {}: {}", e.getClass().getSimpleName(), e.getErrorCode(), e.getDetails());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(e, HttpStatus.NOT_FOUND.value()));
  }

  // 401: Unauthorized (로그인 실패)
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorized(DiscodeitException e) {
    log.warn("[{}] {}: {}", e.getClass().getSimpleName(), e.getErrorCode(), e.getDetails());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse.of(e, HttpStatus.UNAUTHORIZED.value()));
  }

  // 400: Bad Request (중복, PRIVATE 채널 수정 등)
  @ExceptionHandler({
      UserAlreadyExistsException.class,
      DuplicateUserStatusException.class,
      PrivateChannelUpdateException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(DiscodeitException e) {
    log.warn("[{}] {}: {}", e.getClass().getSimpleName(), e.getErrorCode(), e.getDetails());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(e, HttpStatus.BAD_REQUEST.value()));
  }

  // 400: Validation 실패 (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    Map<String, Object> fieldErrors = e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
            (a, b) -> a
        ));
    log.warn("Validation failed: {}", fieldErrors);
    ErrorResponse response = new ErrorResponse(
        java.time.Instant.now(),
        "VALIDATION_FAILED",
        "입력값이 유효하지 않습니다.",
        fieldErrors,
        e.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );
    return ResponseEntity.badRequest().body(response);
  }

  // 500: 그 외 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
    log.error("Unhandled exception", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(e, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", 500));
  }
}
