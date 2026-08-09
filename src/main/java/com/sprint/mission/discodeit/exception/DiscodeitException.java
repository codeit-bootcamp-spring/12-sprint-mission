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
    this(errorCode, details, null);
  }

  // 원인 예외를 감쌀 때 스택트레이스를 잃지 않도록 cause를 전달받는다
  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
    this.details = details != null ? details : Map.of();
  }
}
