package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class DuplicateUserStatusException extends UserException {

  public DuplicateUserStatusException(UUID userId) {
    super(ErrorCode.DUPLICATE_USER_STATUS, Map.of("userId", userId));
  }
}