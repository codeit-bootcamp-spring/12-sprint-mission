package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidRefreshTokenException extends AuthException {

  public InvalidRefreshTokenException() {
    super(ErrorCode.INVALID_REFRESH_TOKEN);
  }

  public static InvalidRefreshTokenException withToken() {
    return new InvalidRefreshTokenException();
  }
}