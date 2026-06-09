package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundByUserIdException extends UserException {

  public UserStatusNotFoundByUserIdException(UUID userId) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of("userId", userId));
  }
}