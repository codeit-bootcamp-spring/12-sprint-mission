package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFound extends UserStatusException {

  public UserStatusNotFound(UUID userStatusId) {
    super(
        ErrorCode.USERSTATUS_NOT_FOUND,
        ErrorCode.USERSTATUS_NOT_FOUND.format(userStatusId),
        Map.of("userStatusId", userStatusId)
    );
  }

  public static UserStatusNotFound withUserId(UUID userId) {
    return new UserStatusNotFound(
        ErrorCode.USERSTATUS_WITH_USERID_NOT_FOUND,
        ErrorCode.USERSTATUS_WITH_USERID_NOT_FOUND.format(userId),
        Map.of("userId", userId)
    );
  }

  private UserStatusNotFound(ErrorCode errorCode, String message, Map<String, Object> details) {
    super(errorCode, message, details);
  }
}
