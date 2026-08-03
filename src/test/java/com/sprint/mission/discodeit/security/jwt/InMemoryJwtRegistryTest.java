package com.sprint.mission.discodeit.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryJwtRegistryTest {

  private static final String SECRET = "discodeit-test-secret-key-must-be-32-bytes-long";

  private final JwtTokenProvider jwtTokenProvider =
      new JwtTokenProvider(SECRET, 1800, 1209600, false);

  private InMemoryJwtRegistry registry(int maxActiveJwtCount) {
    return new InMemoryJwtRegistry(maxActiveJwtCount, jwtTokenProvider);
  }

  private UserDto user() {
    return new UserDto(UUID.randomUUID(), "tester", "tester@email.com", null, false, Role.USER);
  }

  @Test
  @DisplayName("등록한 토큰은 사용자 ID, 엑세스 토큰, 리프레시 토큰 어느 쪽으로도 조회된다")
  void register_thenFindable() {
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation jwtInformation = jwtTokenProvider.generate(user);

    registry.registerJwtInformation(jwtInformation);

    assertThat(registry.hasActiveJwtInformationByUserId(user.id())).isTrue();
    assertThat(registry.hasActiveJwtInformationByAccessToken(jwtInformation.getAccessToken()))
        .isTrue();
    assertThat(registry.hasActiveJwtInformationByRefreshToken(jwtInformation.getRefreshToken()))
        .isTrue();
    assertThat(registry.findJwtInformationByAccessToken(jwtInformation.getAccessToken()))
        .containsSame(jwtInformation);
  }

  @Test
  @DisplayName("최대 동시 로그인 수를 넘기면 가장 오래된 로그인이 밀려난다")
  void register_exceedingMaxActiveCount_evictsOldest() {
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation first = jwtTokenProvider.generate(user);
    JwtInformation second = jwtTokenProvider.generate(user);

    registry.registerJwtInformation(first);
    registry.registerJwtInformation(second);

    assertThat(registry.hasActiveJwtInformationByAccessToken(first.getAccessToken())).isFalse();
    assertThat(registry.hasActiveJwtInformationByAccessToken(second.getAccessToken())).isTrue();
  }

  @Test
  @DisplayName("로테이션하면 이전 토큰 쌍은 조회되지 않는다")
  void rotate_replacesTokenPair() {
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation origin = jwtTokenProvider.generate(user);
    registry.registerJwtInformation(origin);
    JwtInformation renewed = jwtTokenProvider.generate(user);
    // 로테이션은 등록된 객체를 제자리에서 바꾸므로, 비교할 이전 토큰 값을 미리 붙잡아둔다
    String previousRefreshToken = origin.getRefreshToken();

    assertThat(registry.rotateJwtInformation(previousRefreshToken, renewed)).isPresent();

    // 이전 리프레시 토큰이 다시 들어오면 재사용으로 판단할 수 있어야 한다
    assertThat(registry.hasActiveJwtInformationByRefreshToken(previousRefreshToken)).isFalse();
    assertThat(registry.hasActiveJwtInformationByRefreshToken(renewed.getRefreshToken())).isTrue();
    assertThat(registry.hasActiveJwtInformationByAccessToken(renewed.getAccessToken())).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 리프레시 토큰으로 로테이션하면 비어 있는 결과를 반환한다")
  void rotate_withUnknownToken_returnsEmpty() {
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation unknown = jwtTokenProvider.generate(user);

    assertThat(registry.rotateJwtInformation(unknown.getRefreshToken(), unknown)).isEmpty();
  }

  @Test
  @DisplayName("사용자 ID로 무효화하면 해당 사용자의 토큰이 모두 사라진다")
  void invalidateByUserId() {
    InMemoryJwtRegistry registry = registry(2);
    UserDto user = user();
    JwtInformation first = jwtTokenProvider.generate(user);
    JwtInformation second = jwtTokenProvider.generate(user);
    registry.registerJwtInformation(first);
    registry.registerJwtInformation(second);

    registry.invalidateJwtInformationByUserId(user.id());

    assertThat(registry.hasActiveJwtInformationByUserId(user.id())).isFalse();
    assertThat(registry.hasActiveJwtInformationByAccessToken(first.getAccessToken())).isFalse();
    assertThat(registry.hasActiveJwtInformationByAccessToken(second.getAccessToken())).isFalse();
  }

  @Test
  @DisplayName("리프레시 토큰으로 무효화하면 해당 로그인만 사라진다")
  void invalidateByRefreshToken() {
    InMemoryJwtRegistry registry = registry(2);
    UserDto user = user();
    JwtInformation first = jwtTokenProvider.generate(user);
    JwtInformation second = jwtTokenProvider.generate(user);
    registry.registerJwtInformation(first);
    registry.registerJwtInformation(second);

    registry.invalidateJwtInformationByRefreshToken(first.getRefreshToken());

    assertThat(registry.hasActiveJwtInformationByAccessToken(first.getAccessToken())).isFalse();
    assertThat(registry.hasActiveJwtInformationByAccessToken(second.getAccessToken())).isTrue();
  }

  @Test
  @DisplayName("엑세스 토큰이 만료되면 온라인으로 보지 않는다")
  void hasActiveByUserId_withExpiredAccessToken_isFalse() {
    JwtTokenProvider expiringProvider = new JwtTokenProvider(SECRET, -1, 1209600, false);
    InMemoryJwtRegistry registry = new InMemoryJwtRegistry(1, expiringProvider);
    UserDto user = user();
    JwtInformation jwtInformation = expiringProvider.generate(user);

    registry.registerJwtInformation(jwtInformation);

    // 재발급은 가능하지만 접속 중은 아니다
    assertThat(registry.hasActiveJwtInformationByUserId(user.id())).isFalse();
    assertThat(registry.hasActiveJwtInformationByRefreshToken(jwtInformation.getRefreshToken()))
        .isTrue();
  }

  @Test
  @DisplayName("리프레시 토큰까지 만료된 항목만 정리 대상이다")
  void clearExpired_removesOnlyDeadEntries() {
    JwtTokenProvider deadProvider = new JwtTokenProvider(SECRET, -1, -1, false);
    InMemoryJwtRegistry registry = registry(1);
    UserDto alive = user();
    UserDto dead = user();
    JwtInformation aliveInformation = jwtTokenProvider.generate(alive);
    JwtInformation deadInformation = deadProvider.generate(dead);
    registry.registerJwtInformation(aliveInformation);
    registry.registerJwtInformation(deadInformation);

    registry.clearExpiredJwtInformation();

    assertThat(registry.hasActiveJwtInformationByRefreshToken(deadInformation.getRefreshToken()))
        .isFalse();
    assertThat(registry.hasActiveJwtInformationByRefreshToken(aliveInformation.getRefreshToken()))
        .isTrue();
  }
}
