package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class BinaryContentNotFoundException extends
    com.sprint.mission.discodeit.exception.binaryContent.BinaryContentException {

  public BinaryContentNotFoundException() {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
  }

  public static BinaryContentNotFoundException withId(UUID id) {
    BinaryContentNotFoundException exception = new BinaryContentNotFoundException();
    exception.addDetail("id", id);
    return exception;
  }
}
