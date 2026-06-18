package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
    e.printStackTrace();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .code("ILLEGAL_ARGUMENT")
        .message(e.getMessage())
        .details(null)
        .exceptionType(e.getClass().getName())
        .status(HttpStatus.BAD_REQUEST.value())
        .build();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {
    e.printStackTrace();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .code("NO_SUCH_ELEMENT")
        .message(e.getMessage())
        .details(null)
        .exceptionType(e.getClass().getName())
        .status(HttpStatus.NOT_FOUND.value())
        .build();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    e.printStackTrace();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .code("INTERNAL_SERVER_ERROR")
        .message(e.getMessage())
        .details(null)
        .exceptionType(e.getClass().getName())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .build();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
    e.printStackTrace();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(e.getTimestamp())
        .code(e.getErrorCode().name())
        .message(e.getMessage())
        .details(e.getDetails())
        .exceptionType(e.getClass().getName())
        .status(HttpStatus.NOT_FOUND.value())
        .build();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(UserAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException e) {
    e.printStackTrace();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(e.getTimestamp())
        .code(e.getErrorCode().name())
        .message(e.getMessage())
        .details(e.getDetails())
        .exceptionType(e.getClass().getName())
        .status(HttpStatus.BAD_REQUEST.value())
        .build();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleChannelNotFoundException(ChannelNotFoundException e) {
    e.printStackTrace();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(e.getTimestamp())
        .code(e.getErrorCode().name())
        .message(e.getMessage())
        .details(e.getDetails())
        .exceptionType(e.getClass().getName())
        .status(HttpStatus.NOT_FOUND.value())
        .build();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(PrivateChannelUpdateException.class)
  public ResponseEntity<ErrorResponse> handlePrivateChannelUpdateException(PrivateChannelUpdateException e) {
    e.printStackTrace();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(e.getTimestamp())
        .code(e.getErrorCode().name())
        .message(e.getMessage())
        .details(e.getDetails())
        .exceptionType(e.getClass().getName())
        .status(HttpStatus.BAD_REQUEST.value())
        .build();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentValidException(MethodArgumentNotValidException e) {
    e.printStackTrace();
    Map<String, Object> details = new LinkedHashMap<>();
    e.getBindingResult().getFieldErrors().forEach(fieldError -> {
        details.put(fieldError.getField(), fieldError.getDefaultMessage());
    });
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .code("METHOD_ARGUMENT_NOT_VALID")
        .message("입력값 유효성 검증에 실패했습니다.")
        .details(details)
        .exceptionType(e.getClass().getName())
        .status(HttpStatus.BAD_REQUEST.value())
        .build();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }
}
