package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileException extends DiscodeitException {

  public FileException(ErrorCode errorCode, String message, Map<String, Object> details) {
    super(errorCode, message, details);
  }

  public FileException(
      ErrorCode errorCode,
      String message,
      Map<String, Object> details,
      Throwable cause
  ) {
    super(errorCode, message, details, cause);
  }

}
