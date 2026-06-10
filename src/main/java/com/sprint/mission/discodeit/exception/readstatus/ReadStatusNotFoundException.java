package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {

    public ReadStatusNotFoundException(UUID readStatusId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, Map.of("readStatusId", readStatusId));
    }

    public static ReadStatusNotFoundException byUserIdAndChannelId(UUID userId, UUID channelId) {
        return new ReadStatusNotFoundException(userId, channelId);
    }

    private ReadStatusNotFoundException(UUID userId, UUID channelId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, Map.of(
                "userId", userId,
                "channelId", channelId
        ));
    }
}