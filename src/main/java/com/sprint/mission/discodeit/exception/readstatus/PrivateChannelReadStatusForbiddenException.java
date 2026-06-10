package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelReadStatusForbiddenException extends ReadStatusException {

  public PrivateChannelReadStatusForbiddenException(UUID userId, UUID channelId) {
    super(
        ErrorCode.PRIVATE_CHANNEL_READ_STATUS_FORBIDDEN,
        ErrorCode.PRIVATE_CHANNEL_READ_STATUS_FORBIDDEN.format(userId, channelId),
        Map.of(
            "userId", userId,
            "channelId", channelId
        )
    );
  }
}
