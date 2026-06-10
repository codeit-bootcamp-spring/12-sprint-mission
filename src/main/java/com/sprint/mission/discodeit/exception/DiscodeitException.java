package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public DiscodeitException(ErrorCode errorCode, String message, Map<String, Object> details) {
    super(message);
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details == null ? Map.of() : details;
  }

  public DiscodeitException(ErrorCode errorCode, String message, Map<String, Object> details,
      Throwable cause) {
    super(message, cause);
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details == null ? Map.of() : details;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public Map<String, Object> getDetails() {
    return details;
  }

}
