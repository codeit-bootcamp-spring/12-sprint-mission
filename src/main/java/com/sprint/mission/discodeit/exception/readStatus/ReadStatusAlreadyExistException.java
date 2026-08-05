package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class ReadStatusAlreadyExistException extends
    com.sprint.mission.discodeit.exception.readStatus.ReadStatusException {

  public ReadStatusAlreadyExistException() {
    super(ErrorCode.READ_STATUS_DUPLICATE);
  }

  public static ReadStatusAlreadyExistException withUserIdAndChannelId(UUID userId,
      UUID channelId) {
    ReadStatusAlreadyExistException exception = new ReadStatusAlreadyExistException();
    exception.addDetail("userId", userId);
    exception.addDetail("channelId", channelId);
    return exception;
  }
}
