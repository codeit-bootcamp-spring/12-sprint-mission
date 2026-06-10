package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BinaryContentStorageException extends BinaryContentException{

    public BinaryContentStorageException(String operation, Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_STORAGE_ERROR, Map.of("operation", operation), cause);
    }
}
