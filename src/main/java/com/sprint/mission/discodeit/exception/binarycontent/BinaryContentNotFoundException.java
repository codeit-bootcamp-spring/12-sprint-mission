package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException() {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
    }

    public BinaryContentNotFoundException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, cause);
    }

    public static BinaryContentNotFoundException withId(UUID contentId) {
        BinaryContentNotFoundException exception = new BinaryContentNotFoundException();
        exception.addDetail("Content Id", contentId);
        return exception;
    }
}
