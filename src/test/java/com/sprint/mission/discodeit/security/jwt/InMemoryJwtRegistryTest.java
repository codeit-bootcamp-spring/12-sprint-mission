package com.sprint.mission.discodeit.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryJwtRegistryTest {

  private static final String SECRET = "discodeit-test-secret-key-must-be-32-bytes-long";
  private static final long GRACE_SECONDS = 10;

  private final JwtTokenProvider jwtTokenProvider =
      new JwtTokenProvider(SECRET, 1800, 1209600, false);

  private InMemoryJwtRegistry registry(int maxActiveJwtCount) {
    return new InMemoryJwtRegistry(maxActiveJwtCount, GRACE_SECONDS, jwtTokenProvider);
  }

  private UserDto user() {
    return new UserDto(UUID.randomUUID(), "tester", "tester@email.com", null, false, Role.USER);
  }

  private RotationResult rotate(InMemoryJwtRegistry registry, String refreshToken,
      JwtInformation renewed) {
    return registry.rotateJwtInformation(refreshToken, renewed.getAccessToken(),
        renewed.getRefreshToken());
  }

  @Test
  @DisplayName("등록한 토큰은 사용자 ID와 엑세스 토큰으로 조회된다")
  void register_thenFindable() {
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation jwtInformation = jwtTokenProvider.generate(user);

    registry.registerJwtInformation(jwtInformation);

    assertThat(registry.hasActiveJwtInformationByUserId(user.id())).isTrue();
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

    assertThat(registry.findJwtInformationByAccessToken(first.getAccessToken())).isEmpty();
    assertThat(registry.findJwtInformationByAccessToken(second.getAccessToken())).isPresent();
  }

  @Test
  @DisplayName("동시에 로그인해도 제한 수를 넘겨 등록되지 않는다")
  void register_concurrentLogins_respectsMaxActiveCount() throws Exception {
    int maxActive = 2;
    int threads = 16;
    InMemoryJwtRegistry registry = registry(maxActive);
    UserDto user = user();

    List<Callable<Void>> tasks = java.util.stream.IntStream.range(0, threads)
        .<Callable<Void>>mapToObj(i -> () -> {
          registry.registerJwtInformation(jwtTokenProvider.generate(user));
          return null;
        })
        .toList();

    ExecutorService executor = Executors.newFixedThreadPool(8);
    for (Future<Void> future : executor.invokeAll(tasks)) {
      future.get(5, TimeUnit.SECONDS);
    }
    executor.shutdown();

    // add와 초과분 제거가 원자적이지 않으면 제한을 넘기거나 방금 등록한 로그인까지 밀려난다
    assertThat(registry.activeCount(user.id())).isEqualTo(maxActive);
  }

  @Test
  @DisplayName("로테이션하면 새 토큰 쌍이 조회된다")
  void rotate_replacesTokenPair() {
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation origin = jwtTokenProvider.generate(user);
    registry.registerJwtInformation(origin);
    JwtInformation renewed = jwtTokenProvider.generate(user);
    // 로테이션은 등록된 객체의 토큰 쌍을 교체하므로, 비교할 이전 값을 미리 붙잡아둔다
    String previousRefreshToken = origin.getRefreshToken();
    String previousAccessToken = origin.getAccessToken();

    RotationResult result = rotate(registry, previousRefreshToken, renewed);

    assertThat(result.outcome()).isEqualTo(RotationResult.Outcome.ROTATED);
    assertThat(registry.findJwtInformationByAccessToken(renewed.getAccessToken())).isPresent();
    assertThat(registry.findJwtInformationByAccessToken(previousAccessToken)).isEmpty();
  }

  @Test
  @DisplayName("유예 창 안에 직전 리프레시 토큰이 다시 오면 현재 토큰 쌍을 그대로 돌려준다")
  void rotate_replayWithinGraceWindow_returnsCurrentPair() {
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation origin = jwtTokenProvider.generate(user);
    registry.registerJwtInformation(origin);
    String previousRefreshToken = origin.getRefreshToken();

    JwtInformation renewed = jwtTokenProvider.generate(user);
    RotationResult first = rotate(registry, previousRefreshToken, renewed);

    // 두 번째 탭이 같은 리프레시 토큰으로 뒤늦게 도착한 상황
    JwtInformation another = jwtTokenProvider.generate(user);
    RotationResult second = rotate(registry, previousRefreshToken, another);

    assertThat(second.outcome()).isEqualTo(RotationResult.Outcome.GRACE_REPLAY);
    // 재사용으로 오인해 강제 로그아웃시키지 않고, 먼저 발급된 토큰 쌍을 그대로 준다
    assertThat(second.tokens().accessToken()).isEqualTo(first.tokens().accessToken());
    assertThat(registry.findJwtInformationByAccessToken(another.getAccessToken())).isEmpty();
  }

  @Test
  @DisplayName("유예 창이 없으면 직전 토큰 재사용은 그대로 감지된다")
  void rotate_replayWithoutGraceWindow_isMismatch() {
    InMemoryJwtRegistry registry = new InMemoryJwtRegistry(1, 0, jwtTokenProvider);
    UserDto user = user();
    JwtInformation origin = jwtTokenProvider.generate(user);
    registry.registerJwtInformation(origin);
    String previousRefreshToken = origin.getRefreshToken();
    rotate(registry, previousRefreshToken, jwtTokenProvider.generate(user));

    RotationResult replay = rotate(registry, previousRefreshToken,
        jwtTokenProvider.generate(user));

    assertThat(replay.outcome()).isEqualTo(RotationResult.Outcome.MISMATCH);
  }

  @Test
  @DisplayName("동시에 재발급해도 한 번만 로테이션되고 모두 같은 토큰 쌍을 받는다")
  void rotate_concurrentRequests_rotateOnceAndAgree() throws Exception {
    int threads = 8;
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation origin = jwtTokenProvider.generate(user);
    registry.registerJwtInformation(origin);
    String sharedRefreshToken = origin.getRefreshToken();

    List<Callable<RotationResult>> tasks = java.util.stream.IntStream.range(0, threads)
        .<Callable<RotationResult>>mapToObj(i -> () -> {
          JwtInformation renewed = jwtTokenProvider.generate(user);
          return rotate(registry, sharedRefreshToken, renewed);
        })
        .toList();

    ExecutorService executor = Executors.newFixedThreadPool(threads);
    List<RotationResult> results = executor.invokeAll(tasks).stream()
        .map(future -> {
          try {
            return future.get(5, TimeUnit.SECONDS);
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
        })
        .toList();
    executor.shutdown();

    // 정확히 한 스레드만 실제 로테이션에 성공하고, 나머지는 유예 창으로 흡수된다
    assertThat(results).filteredOn(r -> r.outcome() == RotationResult.Outcome.ROTATED).hasSize(1);
    assertThat(results).noneMatch(r -> r.outcome() == RotationResult.Outcome.MISMATCH);
    // 아무도 "자기가 만들지 않은 토큰"을 받지 않도록, 모두가 레지스트리의 현재 값과 일치해야 한다
    String registered = registry.findJwtInformationByAccessToken(
        results.get(0).tokens().accessToken()).orElseThrow().getAccessToken();
    assertThat(results).allMatch(r -> r.tokens().accessToken().equals(registered));
  }

  @Test
  @DisplayName("존재하지 않는 리프레시 토큰으로 로테이션하면 재사용으로 판정한다")
  void rotate_withUnknownToken_isMismatch() {
    InMemoryJwtRegistry registry = registry(1);
    UserDto user = user();
    JwtInformation unknown = jwtTokenProvider.generate(user);

    assertThat(rotate(registry, unknown.getRefreshToken(), unknown).outcome())
        .isEqualTo(RotationResult.Outcome.MISMATCH);
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
    assertThat(registry.findJwtInformationByAccessToken(first.getAccessToken())).isEmpty();
    assertThat(registry.findJwtInformationByAccessToken(second.getAccessToken())).isEmpty();
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

    assertThat(registry.findJwtInformationByAccessToken(first.getAccessToken())).isEmpty();
    assertThat(registry.findJwtInformationByAccessToken(second.getAccessToken())).isPresent();
  }

  @Test
  @DisplayName("엑세스 토큰이 만료되면 온라인으로 보지 않는다")
  void hasActiveByUserId_withExpiredAccessToken_isFalse() {
    JwtTokenProvider expiringProvider = new JwtTokenProvider(SECRET, -1, 1209600, false);
    InMemoryJwtRegistry registry =
        new InMemoryJwtRegistry(1, GRACE_SECONDS, expiringProvider);
    UserDto user = user();
    JwtInformation jwtInformation = expiringProvider.generate(user);

    registry.registerJwtInformation(jwtInformation);

    // 재발급은 가능하지만 접속 중은 아니다
    assertThat(registry.hasActiveJwtInformationByUserId(user.id())).isFalse();
    assertThat(registry.activeCount(user.id())).isEqualTo(1);
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

    assertThat(registry.activeCount(dead.id())).isZero();
    assertThat(registry.activeCount(alive.id())).isEqualTo(1);
  }
}
