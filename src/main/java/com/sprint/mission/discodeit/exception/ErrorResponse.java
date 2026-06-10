package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String code,
    String message,
    String exceptionType,
    Map<String, Object> details
) {

  public static ErrorResponse from(DiscodeitException exception) {
    ErrorCode errorCode = exception.getErrorCode();

    return new ErrorResponse(
        exception.getTimestamp(),
        errorCode.getStatus().value(),
        errorCode.name(),
        exception.getMessage(),
        exception.getClass().getSimpleName(),
        exception.getDetails()
    );
  }

  public static ErrorResponse of(
      int status,
      String code,
      String message,
      String exceptionType,
      Map<String, Object> details
  ) {
    return new ErrorResponse(
        Instant.now(),
        status,
        code,
        message,
        exceptionType,
        details == null ? Map.of() : details
    );
  }
}
