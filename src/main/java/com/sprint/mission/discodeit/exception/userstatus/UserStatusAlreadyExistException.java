package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistException extends UserStatusException {
    public UserStatusAlreadyExistException() {
        super(ErrorCode.DUPLICATE_USER_STATUS);
    }
    public UserStatusAlreadyExistException(Throwable cause) {
        super(ErrorCode.DUPLICATE_USER_STATUS,cause);
    }
}
