package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentDownloadException extends BinaryContentException {
    public BinaryContentDownloadException() {
        super(ErrorCode.FILE_DOWNLOAD_FAILED);
    }
    public BinaryContentDownloadException(Throwable cause) {
        super(ErrorCode.FILE_DOWNLOAD_FAILED, cause);
    }

}
