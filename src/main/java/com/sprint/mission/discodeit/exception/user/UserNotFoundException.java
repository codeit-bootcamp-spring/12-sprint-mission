package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {

  public UserNotFoundException(ErrorCode errorCode,
      String message, Map<String, Object> details) {
    super(errorCode, message, details);
  }

  public static UserNotFoundException withId(UUID id) {
    return new UserNotFoundException(
        ErrorCode.USER_NOT_FOUND,
        ErrorCode.USER_NOT_FOUND.format(id),
        Map.of("userId", id)
    );
  }

  public static UserNotFoundException withUsername(String username) {
    return new UserNotFoundException(
        ErrorCode.USER_NOT_FOUND,
        ErrorCode.USER_NOT_FOUND.format(username),
        Map.of("username", username)
    );
  }

  public static UserNotFoundException withEmail(String email) {
    return new UserNotFoundException(
        ErrorCode.USER_EMAIL_NOT_FOUND,
        ErrorCode.USER_EMAIL_NOT_FOUND.format(email),
        Map.of("email", email)
    );
  }
}