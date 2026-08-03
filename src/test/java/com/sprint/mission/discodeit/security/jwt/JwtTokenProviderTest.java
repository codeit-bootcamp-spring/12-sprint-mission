package com.sprint.mission.discodeit.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private static final String SECRET = "discodeit-test-secret-key-must-be-32-bytes-long";

  private final JwtTokenProvider jwtTokenProvider =
      new JwtTokenProvider(SECRET, 1800, 1209600, false);

  private UserDto user() {
    return new UserDto(UUID.randomUUID(), "tester", "tester@email.com", null, false, Role.USER);
  }

  @Test
  @DisplayName("발급한 토큰은 유효하고 사용자 ID를 복원할 수 있다")
  void generate_thenValid() {
    UserDto user = user();

    JwtInformation jwtInformation = jwtTokenProvider.generate(user);

    assertThat(jwtTokenProvider.isValidAccessToken(jwtInformation.getAccessToken())).isTrue();
    assertThat(jwtTokenProvider.isValidRefreshToken(jwtInformation.getRefreshToken())).isTrue();
    assertThat(jwtTokenProvider.getUserId(jwtInformation.getAccessToken())).isEqualTo(user.id());
  }

  @Test
  @DisplayName("엑세스 토큰을 리프레시 토큰으로는 사용할 수 없다")
  void accessToken_isNotUsableAsRefreshToken() {
    JwtInformation jwtInformation = jwtTokenProvider.generate(user());

    assertThat(jwtTokenProvider.isValidRefreshToken(jwtInformation.getAccessToken())).isFalse();
    assertThat(jwtTokenProvider.isValidAccessToken(jwtInformation.getRefreshToken())).isFalse();
  }

  @Test
  @DisplayName("다른 키로 서명된 토큰은 검증에 실패한다")
  void tokenSignedWithAnotherKey_isInvalid() {
    JwtTokenProvider another =
        new JwtTokenProvider("another-secret-key-that-is-long-enough-32", 1800, 1209600, false);
    JwtInformation jwtInformation = another.generate(user());

    assertThat(jwtTokenProvider.isValidAccessToken(jwtInformation.getAccessToken())).isFalse();
    assertThat(jwtTokenProvider.getUserId(jwtInformation.getAccessToken())).isNull();
  }

  @Test
  @DisplayName("만료된 토큰은 유효하지 않다")
  void expiredToken_isInvalid() {
    JwtTokenProvider expiring = new JwtTokenProvider(SECRET, -1, -1, false);
    JwtInformation jwtInformation = expiring.generate(user());

    assertThat(expiring.isValidAccessToken(jwtInformation.getAccessToken())).isFalse();
    assertThat(expiring.isExpired(jwtInformation.getRefreshToken())).isTrue();
  }

  @Test
  @DisplayName("같은 사용자에게 발급해도 토큰 문자열은 매번 달라진다")
  void generate_producesUniqueTokens() {
    UserDto user = user();

    JwtInformation first = jwtTokenProvider.generate(user);
    JwtInformation second = jwtTokenProvider.generate(user);

    // 레지스트리가 토큰 문자열로 항목을 구분하므로 겹치면 상태가 꼬인다
    assertThat(first.getAccessToken()).isNotEqualTo(second.getAccessToken());
    assertThat(first.getRefreshToken()).isNotEqualTo(second.getRefreshToken());
  }

  @Test
  @DisplayName("HS256 최소 키 길이를 만족하지 못하면 기동에 실패한다")
  void shortSecret_throws() {
    assertThatThrownBy(() -> new JwtTokenProvider("too-short", 1800, 1209600, false))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("32바이트");
  }

  @Test
  @DisplayName("리프레시 토큰 쿠키는 HttpOnly로 발급되고, 삭제용 쿠키는 maxAge가 0이다")
  void refreshTokenCookie() {
    JwtInformation jwtInformation = jwtTokenProvider.generate(user());

    assertThat(jwtTokenProvider.createRefreshTokenCookie(jwtInformation.getRefreshToken())
        .isHttpOnly()).isTrue();
    assertThat(jwtTokenProvider.createExpiredRefreshTokenCookie().getMaxAge().isZero()).isTrue();
  }
}
