package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class ChannelAccessDeniedException extends ChannelException {

  public ChannelAccessDeniedException() {
    super(ErrorCode.CHANNEL_ACCESS_DENIED);
  }

  public static ChannelAccessDeniedException withChannelIdAndUserId(UUID channelId, UUID userId) {
    ChannelAccessDeniedException exception = new ChannelAccessDeniedException();
    exception.addDetail("channelId", channelId);
    exception.addDetail("userId", userId);
    return exception;
  }


}
