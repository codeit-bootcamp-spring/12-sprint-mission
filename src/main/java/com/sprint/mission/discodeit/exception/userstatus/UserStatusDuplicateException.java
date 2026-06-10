package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusDuplicateException extends UserStatusException {

  public static UserStatusDuplicateException withUserId(UUID userId) {
    return new UserStatusDuplicateException(
        ErrorCode.USERSTATUS_USER_ID_ALREADY_EXIST,
        ErrorCode.USERSTATUS_USER_ID_ALREADY_EXIST.format(userId),
        Map.of("userId", userId)
    );
  }

  private UserStatusDuplicateException(ErrorCode errorCode, String message,
      Map<String, Object> details) {
    super(errorCode, message, details);
  }

}
