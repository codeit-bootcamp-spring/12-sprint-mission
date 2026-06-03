package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class UserStatusAlreadyExistException extends UserStatusException {

  public UserStatusAlreadyExistException() {
    super(ErrorCode.USER_STATUS_DUPLICATE);
  }

  public static UserStatusAlreadyExistException withUserId(UUID userId) {
    UserStatusAlreadyExistException exception = new UserStatusAlreadyExistException();
    exception.addDetail("userId", userId);
    return exception;
  }

}
