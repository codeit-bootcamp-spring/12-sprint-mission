package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

// 모든 도메인 예외의 기반 클래스
// details: 예외 발생 컨텍스트 정보 (조회 ID, 잘못된 값 등)를 담아 디버깅에 활용
@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp = Instant.now();
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = details != null ? details : Map.of();
  }
}
