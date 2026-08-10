package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidRefreshTokenException extends DiscodeitException {

  public InvalidRefreshTokenException() {
    super(ErrorCode.INVALID_REFRESH_TOKEN);
  }
}