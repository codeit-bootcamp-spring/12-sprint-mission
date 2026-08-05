package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class JwtException extends DiscodeitException {

  public JwtException(ErrorCode errorCode) {
    super(errorCode, errorCode.getMessage(), Map.of());
  }

  public JwtException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, errorCode.getMessage(), Map.of(), cause);
  }
}
