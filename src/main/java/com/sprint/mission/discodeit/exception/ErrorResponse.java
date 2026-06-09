package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {
  public static ErrorResponse of(DiscodeitException e, int status) {
    return new ErrorResponse(
        e.getTimestamp(),
        e.getErrorCode().name(),
        e.getErrorCode().getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        status
    );
  }

  public static ErrorResponse of(Exception e, String code, String message, int status) {
    return new ErrorResponse(
        Instant.now(),
        code,
        message,
        Map.of(),
        e.getClass().getSimpleName(),
        status
    );
  }
}
