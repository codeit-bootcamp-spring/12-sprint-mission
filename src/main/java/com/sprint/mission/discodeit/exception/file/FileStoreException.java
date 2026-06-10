package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;

public class FileStoreException extends FileException {

  public FileStoreException(String targetName, String operation, String path, Throwable cause) {
    super(
        ErrorCode.FILE_PROCESSING_ERROR,
        ErrorCode.FILE_PROCESSING_ERROR.getMessage(),
        createDetails(targetName, operation, path),
        cause
    );
  }

  private static Map<String, Object> createDetails(
      String targetName,
      String operation,
      String path
  ) {
    Map<String, Object> details = new HashMap<>();
    details.put("targetName", targetName);
    details.put("operation", operation);
    details.put("path", path);
    return details;
  }

}
