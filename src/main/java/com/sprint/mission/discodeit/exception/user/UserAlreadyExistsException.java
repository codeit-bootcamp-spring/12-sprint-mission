package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException(ErrorCode errorCode,
      String message, Map<String, Object> details) {
    super(errorCode, message, details);
  }

  public static UserAlreadyExistsException withEmail(String email) {
    return new UserAlreadyExistsException(
        ErrorCode.USER_EMAIL_ALREADY_EXISTS,
        ErrorCode.USER_EMAIL_ALREADY_EXISTS.format(email),
        Map.of("email", email)
    );
  }

  public static UserAlreadyExistsException withUsername(String username) {
    return new UserAlreadyExistsException(
        ErrorCode.USER_USERNAME_ALREADY_EXISTS,
        ErrorCode.USER_USERNAME_ALREADY_EXISTS.format(username),
        Map.of("username", username)
    );
  }
}