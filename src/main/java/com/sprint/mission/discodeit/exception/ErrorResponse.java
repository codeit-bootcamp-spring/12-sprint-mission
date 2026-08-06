package com.sprint.mission.discodeit.exception;

import java.util.Map;

public record ErrorResponse(
        String code,
    String message
) {

  public static ErrorResponse from(DiscodeitException exception) {
    ErrorCode errorCode = exception.getErrorCode();

    return new ErrorResponse(
            errorCode.name(),                       // code
        exception.getMessage()                 // message
            // details
            // exceptionType
            // status
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
            code,
        message
    );
  }
}
