package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateException extends ChannelException {

  public PrivateChannelUpdateException(ErrorCode errorCode,
      String message, Map<String, Object> details) {
    super(errorCode, message, details);
  }

  public static PrivateChannelUpdateException forChannel(UUID channelId) {
    return new PrivateChannelUpdateException(
        ErrorCode.PRIVATE_CHANNEL_CANNOT_UPDATE,
        "Private channel cannot be updated: " + channelId,
        Map.of("channelId", channelId)
    );
  }
}