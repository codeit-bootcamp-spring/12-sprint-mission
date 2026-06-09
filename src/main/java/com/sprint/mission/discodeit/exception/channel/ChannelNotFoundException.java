package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException {

  public ChannelNotFoundException(ErrorCode errorCode,
      String message, Map<String, Object> details) {
    super(errorCode, message, details);
  }

  public static ChannelNotFoundException withId(UUID channelId) {
    return new ChannelNotFoundException(
        ErrorCode.CHANNEL_NOT_FOUND,
        "Channel not found: " + channelId,
        Map.of("channelId", channelId)
    );
  }
}