package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentAlreadyExistException extends BinaryContentException {
    public BinaryContentAlreadyExistException() {
        super(ErrorCode.DUPLICATE_BINARY_CONTENT);
    }

    public BinaryContentAlreadyExistException(Throwable cause) {
        super(ErrorCode.DUPLICATE_BINARY_CONTENT, cause);
    }
}
