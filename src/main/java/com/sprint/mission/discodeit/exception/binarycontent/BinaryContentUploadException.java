package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentUploadException extends BinaryContentException {
    public BinaryContentUploadException() {
        super(ErrorCode.FILE_UPLOAD_FAILED);
    }

    public BinaryContentUploadException(Throwable cause) {
        super(ErrorCode.FILE_UPLOAD_FAILED,cause);
    }


}
