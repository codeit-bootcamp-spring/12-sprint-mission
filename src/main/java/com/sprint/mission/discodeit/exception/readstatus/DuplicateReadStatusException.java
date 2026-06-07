package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateReadStatusException extends ReadStatusException {

  public DuplicateReadStatusException() {
    super(ErrorCode.DUPLICATE_READ_STATUS);
  }
}