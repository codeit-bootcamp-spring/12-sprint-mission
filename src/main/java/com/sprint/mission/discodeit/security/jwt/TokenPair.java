package com.sprint.mission.discodeit.security.jwt;

import java.time.Instant;

/**
 * 한 로그인이 보유한 토큰 쌍. 항상 함께 갱신되므로 하나의 불변 값으로 묶는다.
 *
 * <p>두 토큰을 각각의 가변 필드로 두면 다른 스레드가 {새 엑세스 토큰, 옛 리프레시 토큰} 같은
 * 찢어진 조합을 관측할 수 있다. 불변 객체 참조 하나만 원자적으로 교체하면 그런 중간 상태가 없다.
 *
 * @param previousRefreshToken          직전 리프레시 토큰. 유예 창 안의 중복 재발급 요청을 정상 요청으로
 *                                      인정하기 위해 짧게 남겨둔다.
 * @param previousRefreshTokenExpiresAt 유예 창의 끝. 이 시각을 넘긴 직전 토큰은 재사용으로 본다.
 */
public record TokenPair(
    String accessToken,
    String refreshToken,
    String previousRefreshToken,
    Instant previousRefreshTokenExpiresAt
) {

  public static TokenPair of(String accessToken, String refreshToken) {
    return new TokenPair(accessToken, refreshToken, null, null);
  }

  TokenPair rotateTo(String newAccessToken, String newRefreshToken, Instant graceExpiresAt) {
    return new TokenPair(newAccessToken, newRefreshToken, this.refreshToken, graceExpiresAt);
  }

  boolean matchesCurrent(String candidate) {
    return refreshToken.equals(candidate);
  }

  boolean matchesPreviousWithinGrace(String candidate, Instant now) {
    return previousRefreshToken != null
        && previousRefreshToken.equals(candidate)
        && previousRefreshTokenExpiresAt != null
        && now.isBefore(previousRefreshTokenExpiresAt);
  }
}
