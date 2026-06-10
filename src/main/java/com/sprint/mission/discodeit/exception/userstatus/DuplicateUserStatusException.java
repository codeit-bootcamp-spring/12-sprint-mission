package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateUserStatusException extends UserStatusException {

  public DuplicateUserStatusException() {
    super(ErrorCode.DUPLICATE_USER_STATUS);
  }
}