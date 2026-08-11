package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class JwtGenerationException extends AuthException {

  public JwtGenerationException(Throwable cause) {
    super(ErrorCode.JWT_GENERATION_FAILED, cause);
  }
}