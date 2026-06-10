package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusDuplicateException extends ReadStatusException {

  public static ReadStatusDuplicateException withUserIdAndChannelId(UUID userId, UUID channelId) {
    return new ReadStatusDuplicateException(
        ErrorCode.READSTATUS_ALREADY_EXIST,
        ErrorCode.READSTATUS_ALREADY_EXIST.format(userId, channelId),
        Map.of(
            "userId", userId,
            "channelId", channelId
        ));
  }

  private ReadStatusDuplicateException(ErrorCode errorCode, String message,
      Map<String, Object> details) {
    super(errorCode, message, details);
  }
}
