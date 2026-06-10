package com.sprint.mission.discodeit.exception;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception
  ) {
    Map<String, Object> details = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            error -> error.getField(),
            error -> error.getDefaultMessage(),
            (existing, replacement) -> existing
        ));

    ErrorResponse response = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        "잘못된 요청입니다.",
        exception.getClass().getSimpleName(),
        details
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
    ErrorResponse response = ErrorResponse.from(exception);

    return ResponseEntity
        .status(exception.getErrorCode().getStatus())
        .body(response);
  }

}
