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

  public static ErrorResponse from(DiscodeitException exception) {
    ErrorCode errorCode = exception.getErrorCode();

    return new ErrorResponse(
        exception.getTimestamp(),
        errorCode.name(),                       // code
        exception.getMessage(),                 // message
        exception.getDetails(),                 // details
        exception.getClass().getSimpleName(),   // exceptionType
        errorCode.getStatus().value()           // status
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
        code,
        message,
        details == null ? Map.of() : details,
        exceptionType,
        status
    );
  }
}
