package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class StorageException extends DiscodeitException {

  public StorageException(String operation, UUID id) {
    super(ErrorCode.STORAGE_FAILURE, Map.of("operation", operation, "id", id));
  }

  public StorageException(String operation) {
    super(ErrorCode.STORAGE_FAILURE, Map.of("operation", operation));
  }
}
