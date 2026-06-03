package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {

  public UserStatusNotFoundException() {
    super(ErrorCode.USER_STATUS_NOT_FOUND);
  }

  public static UserStatusNotFoundException withId(UUID id) {
    UserStatusNotFoundException exception = new UserStatusNotFoundException();
    exception.addDetail("id", id);
    return exception;
  }
}
