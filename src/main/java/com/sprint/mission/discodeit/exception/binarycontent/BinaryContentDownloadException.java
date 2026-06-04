package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class BinaryContentDownloadException extends BinaryContentException {
    public BinaryContentDownloadException() {
        super(ErrorCode.FILE_DOWNLOAD_FAILED);
    }

    public BinaryContentDownloadException(Throwable cause) {
        super(ErrorCode.FILE_DOWNLOAD_FAILED, cause);
    }

    public static BinaryContentAlreadyExistException withId(UUID binaryContentId) {
        BinaryContentAlreadyExistException exception = new BinaryContentAlreadyExistException();
        exception.addDetail("binaryContentId", binaryContentId);
        return exception;
    }
}
