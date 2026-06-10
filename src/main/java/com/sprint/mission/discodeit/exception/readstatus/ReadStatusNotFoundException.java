package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {
    public ReadStatusNotFoundException() {
        super(ErrorCode.READ_STATUS_NOT_FOUND);
    }

    public ReadStatusNotFoundException(Throwable cause) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, cause);
    }

    public static ReadStatusNotFoundException withUserIdAndChannelId(UUID userId, UUID channelId) {
        ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
        exception.addDetail("userId", userId);
        exception.addDetail("channelId", channelId);
        return exception;
    }

    public static ReadStatusNotFoundException withId(UUID id) {
        ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
        exception.addDetail("ReadStatusId", id);
        return exception;
    }
}
