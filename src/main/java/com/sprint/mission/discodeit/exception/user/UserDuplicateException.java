package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserDuplicateException extends UserException {

  public static UserDuplicateException withEmail(String email) {
    return new UserDuplicateException(
        ErrorCode.USER_EMAIL_ALREADY_EXISTS,
        ErrorCode.USER_EMAIL_ALREADY_EXISTS.format(email),
        Map.of("email", email)
    );
  }

  public static UserDuplicateException withUsername(String username) {
    return new UserDuplicateException(
        ErrorCode.USER_USERNAME_ALREADY_EXISTS,
        ErrorCode.USER_USERNAME_ALREADY_EXISTS.format(username),
        Map.of("username", username));
  }

  private UserDuplicateException(ErrorCode errorCode, String message, Map<String, Object> details) {
    super(errorCode, message, details);
  }
}
