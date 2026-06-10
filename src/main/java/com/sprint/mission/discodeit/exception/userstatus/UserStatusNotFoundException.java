package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {

    public UserStatusNotFoundException() {
        super(ErrorCode.USER_STATUS_NOT_FOUND);
    }
    public UserStatusNotFoundException(Throwable cause) {
        super(ErrorCode.USER_STATUS_NOT_FOUND,cause);
    }

    public static UserStatusNotFoundException withId(UUID id) {
        UserStatusNotFoundException exception = new UserStatusNotFoundException();
        exception.addDetail("UserStatusId", id);
        return exception;
    }

    public static UserStatusNotFoundException withUserId(UUID userId) {
        UserStatusNotFoundException exception = new UserStatusNotFoundException();
        exception.addDetail("UserId", userId);
        return exception;
    }
}
