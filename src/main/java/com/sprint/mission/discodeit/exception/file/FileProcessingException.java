package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileProcessingException extends FileException {

  public FileProcessingException(String fileName, String contentType, long size, Throwable cause) {
    super(
        ErrorCode.FILE_PROCESSING_ERROR,
        ErrorCode.FILE_PROCESSING_ERROR.getMessage(),
        createDetails(fileName, contentType, size),
        cause
    );
  }

  public FileProcessingException(String operation, String root, Throwable cause) {
    super(
        ErrorCode.FILE_PROCESSING_ERROR,
        ErrorCode.FILE_PROCESSING_ERROR.getMessage(),
        createStorageDetails(operation, null, root),
        cause
    );
  }

  public FileProcessingException(String operation, UUID binaryContentId, String root,
      Throwable cause) {
    super(
        ErrorCode.FILE_PROCESSING_ERROR,
        ErrorCode.FILE_PROCESSING_ERROR.getMessage(),
        createStorageDetails(operation, binaryContentId, root),
        cause
    );
  }

  private static Map<String, Object> createDetails(String fileName, String contentType, long size) {
    Map<String, Object> details = new HashMap<>();
    details.put("fileName", fileName);
    details.put("contentType", contentType);
    details.put("size", size);
    return details;
  }

  private static Map<String, Object> createStorageDetails(
      String operation,
      UUID binaryContentId,
      String root
  ) {
    Map<String, Object> details = new HashMap<>();
    details.put("operation", operation);
    details.put("binaryContentId", binaryContentId);
    details.put("root", root);
    return details;
  }

}
