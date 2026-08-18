package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.storage.InvalidFileUploadException;
import com.sprint.mission.discodeit.exception.storage.StorageException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
      ReadStatusNotFoundException.class
  })
  public ResponseEntity<ErrorResponse> handleNotFound(DiscodeitException e) {
    log.warn("[{}] {}: {}", e.getClass().getSimpleName(), e.getErrorCode(), e.getDetails(), e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(e, HttpStatus.NOT_FOUND.value()));
  }

  // 401: Unauthorized (로그인 실패, 리프레시 토큰 무효)
  @ExceptionHandler({
      InvalidCredentialsException.class,
      InvalidRefreshTokenException.class
  })
  public ResponseEntity<ErrorResponse> handleUnauthorized(DiscodeitException e) {
    log.warn("[{}] {}: {}", e.getClass().getSimpleName(), e.getErrorCode(), e.getDetails(), e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse.of(e, HttpStatus.UNAUTHORIZED.value()));
  }

  // 400: Bad Request (중복, PRIVATE 채널 수정 등)
  @ExceptionHandler({
      UserAlreadyExistsException.class,
      PrivateChannelUpdateException.class,
      InvalidFileUploadException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(DiscodeitException e) {
    log.warn("[{}] {}: {}", e.getClass().getSimpleName(), e.getErrorCode(), e.getDetails(), e);
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

  // 403: 권한 없음 (Method Security에서 던진 예외가 Controller 밖으로 전파된 경우)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
    log.warn("접근 권한 없음: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse.of(e, "ACCESS_DENIED", "접근 권한이 없습니다.",
            HttpStatus.FORBIDDEN.value()));
  }

  // 500: 스토리지 오류
  @ExceptionHandler(StorageException.class)
  public ResponseEntity<ErrorResponse> handleStorage(DiscodeitException e) {
    log.error("[{}] {}: {}", e.getClass().getSimpleName(), e.getErrorCode(), e.getDetails(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(e, HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  // 404: 매핑된 핸들러도 정적 리소스도 없는 경로.
  // 아래 Exception 핸들러가 먼저 잡으면 없는 파일 요청이 500 INTERNAL_ERROR로 나간다.
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException e) {
    log.warn("존재하지 않는 리소스: {}", e.getResourcePath());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(e, "RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND.value()));
  }

  // 500: 그 외 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
    log.error("Unhandled exception", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(e, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", 500));
  }
}
