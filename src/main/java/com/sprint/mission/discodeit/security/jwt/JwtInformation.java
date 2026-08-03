package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Getter;

/**
 * 발급된 토큰 한 쌍의 상태.
 *
 * <p>JwtRegistry에 등록되어 "현재 살아있는 로그인"을 표현한다. 토큰 자체는 무상태이지만, 로그아웃/강제
 * 만료/동시 로그인 제한을 하려면 서버가 발급 사실을 알고 있어야 하므로 이 객체를 보관한다.
 */
@Getter
public class JwtInformation {

  private final UserDto userDto;
  // 로테이션으로 교체되므로 final이 아니다
  private String accessToken;
  private String refreshToken;

  public JwtInformation(UserDto userDto, String accessToken, String refreshToken) {
    this.userDto = userDto;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  /**
   * 토큰 재발급 시 보관 중인 토큰 쌍을 새 토큰으로 교체한다.
   *
   * <p>새 객체로 갈아끼우지 않고 제자리에서 교체하는 이유는, 레지스트리의 Queue 안에서 순서(가장 오래된
   * 로그인)를 유지해야 하기 때문이다.
   */
  public void rotate(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
