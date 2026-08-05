package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class AccessDeniedAuthException extends AuthException {

  public AccessDeniedAuthException() {
    super(ErrorCode.ACCESS_DENIED);
  }
}
