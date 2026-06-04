package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class BinaryContentUploadException extends BinaryContentException {
    public BinaryContentUploadException() {
        super(ErrorCode.FILE_UPLOAD_FAILED);
    }

    public BinaryContentUploadException(Throwable cause) {
        super(ErrorCode.FILE_UPLOAD_FAILED,cause);
    }

    public static BinaryContentUploadException withId(UUID binaryContentId) {
        BinaryContentUploadException exception = new BinaryContentUploadException();
        exception.addDetail("binaryContentId",binaryContentId);
        return exception;
    }

}
