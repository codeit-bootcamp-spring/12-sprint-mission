package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

  public MessageNotFoundException(UUID messageId) {
    super(
        ErrorCode.MESSAGE_NOT_FOUND,
        ErrorCode.MESSAGE_NOT_FOUND.format(messageId),
        Map.of("messageId", messageId)
    );
  }

}
