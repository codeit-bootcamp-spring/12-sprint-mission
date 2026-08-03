package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class InvalidRefreshTokenException extends DiscodeitException {

  public InvalidRefreshTokenException(String reason) {
    super(ErrorCode.INVALID_REFRESH_TOKEN, Map.of("reason", reason));
  }
}
