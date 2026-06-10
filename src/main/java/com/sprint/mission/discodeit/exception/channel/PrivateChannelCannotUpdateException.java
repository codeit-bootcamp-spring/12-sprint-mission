package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelCannotUpdateException extends ChannelException {

  public PrivateChannelCannotUpdateException(UUID channelId) {
    super(
        ErrorCode.PRIVATE_CHANNEL_CANNOT_UPDATE,
        ErrorCode.PRIVATE_CHANNEL_CANNOT_UPDATE.format(channelId),
        Map.of("channelId", channelId)
    );
  }

}
