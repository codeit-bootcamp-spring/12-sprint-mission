package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;

/**
 * 발급된 토큰 한 쌍의 상태.
 *
 * <p>JwtRegistry에 등록되어 "현재 살아있는 로그인"을 표현한다. 토큰 자체는 무상태이지만, 로그아웃/강제
 * 만료/동시 로그인 제한을 하려면 서버가 발급 사실을 알고 있어야 하므로 이 객체를 보관한다.
 *
 * <p>이 객체는 여러 스레드가 동시에 만진다 — 요청 스레드의 필터 검증, 재발급 스레드의 로테이션,
 * 스케줄러 스레드의 만료 정리. 그래서 토큰 쌍을 불변 값으로 묶고 참조만 CAS로 교체한다.
 * ConcurrentLinkedQueue가 주는 happens-before는 큐의 노드 추가/제거에 대한 것이지, 이미 큐에 들어
 * 있는 객체의 내부 필드 변경에는 적용되지 않기 때문이다.
 */
@Getter
public class JwtInformation {

  private final UserDto userDto;
  private final AtomicReference<TokenPair> tokens;

  public JwtInformation(UserDto userDto, String accessToken, String refreshToken) {
    this.userDto = userDto;
    this.tokens = new AtomicReference<>(TokenPair.of(accessToken, refreshToken));
  }

  public String getAccessToken() {
    return tokens.get().accessToken();
  }

  public String getRefreshToken() {
    return tokens.get().refreshToken();
  }

  /**
   * 주어진 리프레시 토큰이 현재 토큰과 일치할 때만 새 토큰 쌍으로 교체한다.
   *
   * <p>검사와 교체를 CAS 한 번으로 합쳐, 두 요청이 동시에 들어와도 한쪽만 로테이션에 성공한다.
   * 실패한 쪽은 유예 창 안이라면 이미 교체된 현재 토큰 쌍을 그대로 돌려받는다. 이렇게 하지 않으면
   * 정상 사용자가 탭을 두 개 열어둔 것만으로 재사용 감지에 걸려 강제 로그아웃된다.
   */
  public RotationResult rotateIfMatches(String expectedRefreshToken, String newAccessToken,
      String newRefreshToken, Duration graceWindow) {
    while (true) {
      TokenPair current = tokens.get();

      if (current.matchesCurrent(expectedRefreshToken)) {
        TokenPair rotated = current.rotateTo(newAccessToken, newRefreshToken,
            Instant.now().plus(graceWindow));
        if (tokens.compareAndSet(current, rotated)) {
          return new RotationResult(RotationResult.Outcome.ROTATED, rotated);
        }
        // 그 사이 다른 스레드가 교체했다. 새 상태를 다시 읽어 판정한다.
        continue;
      }

      if (current.matchesPreviousWithinGrace(expectedRefreshToken, Instant.now())) {
        return new RotationResult(RotationResult.Outcome.GRACE_REPLAY, current);
      }

      return RotationResult.mismatch();
    }
  }
}
