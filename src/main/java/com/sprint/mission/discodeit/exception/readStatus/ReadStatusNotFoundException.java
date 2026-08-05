package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class ReadStatusNotFoundException extends
    com.sprint.mission.discodeit.exception.readStatus.ReadStatusException {

  public ReadStatusNotFoundException() {
    super(ErrorCode.READ_STATUS_NOT_FOUND);
  }

  public static ReadStatusNotFoundException withId(UUID id) {
    ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
    exception.addDetail("id", id);
    return exception;
  }

}
