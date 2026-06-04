package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;


public class ReadStatusAlreadyExistException extends ReadStatusException {
    public ReadStatusAlreadyExistException() {
        super(ErrorCode.DUPLICATE_READ_STATUS);
    }

    public ReadStatusAlreadyExistException(Throwable cause) {
        super(ErrorCode.DUPLICATE_READ_STATUS,cause);
    }
}
