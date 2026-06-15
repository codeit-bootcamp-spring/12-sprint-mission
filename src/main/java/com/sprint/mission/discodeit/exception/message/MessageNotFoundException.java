package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

  public MessageNotFoundException(ErrorCode errorCode,
      String message, Map<String, Object> details) {
    super(errorCode, message, details);
  }

  public static MessageNotFoundException withId(UUID messageId) {
    return new MessageNotFoundException(
        ErrorCode.MESSAGE_NOT_FOUND,
        "Message not found: " + messageId,
        Map.of("messageId", messageId)
    );
  }
}