package com.sprint.mission.discodeit.security.jwt;

/**
 * 리프레시 토큰 로테이션 시도의 결과.
 *
 * <p>"활성 토큰인지 검사한 뒤 교체"하는 두 단계로 나누면 그 사이에 다른 스레드가 끼어들 수 있다.
 * 검사와 교체를 한 번의 원자적 연산으로 합치고, 그 결과 하나로 판정하기 위한 타입이다.
 */
public record RotationResult(Outcome outcome, TokenPair tokens) {

  public enum Outcome {
    /** 정상 로테이션. 새 토큰 쌍이 등록되었다. */
    ROTATED,
    /**
     * 유예 창 안에 도착한 직전 리프레시 토큰. 탭 여러 개가 동시에 재발급을 요청한 경우로,
     * 다시 로테이션하지 않고 현재 토큰 쌍을 그대로 돌려준다(멱등).
     */
    GRACE_REPLAY,
    /** 어느 쪽과도 맞지 않음. 이미 무효화되었거나 탈취 후 재사용된 토큰이다. */
    MISMATCH
  }

  public static RotationResult mismatch() {
    return new RotationResult(Outcome.MISMATCH, null);
  }

  public boolean isAccepted() {
    return outcome != Outcome.MISMATCH;
  }
}
