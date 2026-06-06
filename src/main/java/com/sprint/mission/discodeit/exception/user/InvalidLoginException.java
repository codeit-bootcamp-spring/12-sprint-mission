package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidLoginException extends UserException {

  public InvalidLoginException(String username) {
    super(ErrorCode.INVALID_LOGIN, Map.of("username", username));
  }

}
