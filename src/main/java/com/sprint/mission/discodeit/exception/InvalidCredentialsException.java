package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class InvalidCredentialsException extends DiscodeitException {

  public InvalidCredentialsException(String username) {
    super(ErrorCode.INVALID_CREDENTIALS, Map.of("username", username));
  }
}
