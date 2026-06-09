package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import java.util.Map;
import java.util.UUID;

public class DuplicateUserStatusException extends UserException {

  public DuplicateUserStatusException(UUID userId) {
    super(ErrorCode.DUPLICATE_USER_STATUS, Map.of("userId", userId));
  }
}