package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundByUserIdException extends UserException {

  public UserStatusNotFoundByUserIdException(UUID userId) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of("userId", userId));
  }
}