package com.sprint.mission.discodeit.security.jwt;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 메모리에 JwtInformation을 보관하는 JwtRegistry 구현체.
 *
 * <p>서버 메모리에 두므로 인스턴스를 늘리면 로그인한 서버에서만 토큰이 유효해진다. 다중 인스턴스로
 * 확장하려면 Redis 같은 공유 저장소 기반 구현체로 교체해야 한다.
 *
 * <p>큐를 바꾸는 연산은 모두 {@code compute}/{@code computeIfPresent} 안에서 수행해 사용자 단위로
 * 직렬화한다. "추가한 뒤 초과분을 밀어낸다"처럼 여러 단계로 이루어진 연산은 각 단계가 스레드 안전해도
 * 전체가 원자적이지는 않기 때문이다. 읽기 연산은 락 없이 큐를 순회한다.
 */
@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  // <userId, Queue<JwtInformation>>
  // 토큰 문자열이 아니라 userId를 키로 두면 사용자 단위 조회·무효화가 O(1)이다.
  // (큐 자체의 size()는 O(n)이므로 동시 로그인 수 제어는 O(활성 로그인 수)다)
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;
  private final Duration refreshGraceWindow;
  private final JwtTokenProvider jwtTokenProvider;

  public InMemoryJwtRegistry(
      @Value("${discodeit.security.jwt.max-active-count}") int maxActiveJwtCount,
      @Value("${discodeit.security.jwt.refresh-grace-seconds}") long refreshGraceSeconds,
      JwtTokenProvider jwtTokenProvider
  ) {
    this.maxActiveJwtCount = maxActiveJwtCount;
    this.refreshGraceWindow = Duration.ofSeconds(refreshGraceSeconds);
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.getUserDto().id();

    // 추가와 초과분 제거를 하나의 compute 안에서 처리한다. 나누면 동시 로그인 두 건이
    // 서로의 등록을 밀어내거나, 둘 다 제한을 넘긴 채 통과할 수 있다.
    origin.compute(userId, (key, queue) -> {
      Queue<JwtInformation> target = queue == null ? new ConcurrentLinkedQueue<>() : queue;
      target.add(jwtInformation);
      while (target.size() > maxActiveJwtCount) {
        target.poll();
        log.info("동시 로그인 수 초과로 기존 로그인 무효화: userId={}", userId);
      }
      log.debug("토큰 등록: userId={}, activeCount={}", userId, target.size());
      return target;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> removed = origin.remove(userId);
    if (removed != null && !removed.isEmpty()) {
      log.info("사용자의 모든 토큰 무효화: userId={}, count={}", userId, removed.size());
    }
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    UUID userId = jwtTokenProvider.getUserId(refreshToken);
    if (userId == null) {
      return;
    }
    origin.computeIfPresent(userId, (key, queue) -> {
      if (queue.removeIf(info -> info.getRefreshToken().equals(refreshToken))) {
        log.info("리프레시 토큰으로 로그인 무효화: userId={}", userId);
      }
      return queue.isEmpty() ? null : queue;
    });
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue == null) {
      return false;
    }
    // 리프레시 토큰은 2주를 살기 때문에, 온라인 판단은 엑세스 토큰이 살아있는지로 한다.
    // 다만 토큰은 발급 이후 아무 신호도 주지 않으므로, 탭을 닫아도 엑세스 토큰 수명 동안은
    // online으로 보인다. 더 정확히 하려면 요청 시각을 따로 기록해야 한다.
    return queue.stream()
        .anyMatch(info -> !jwtTokenProvider.isExpired(info.getAccessToken()));
  }

  /** 해당 사용자에게 등록된 로그인 수. 동시 로그인 제어가 지켜졌는지 확인할 때 쓴다. */
  public int activeCount(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue == null ? 0 : queue.size();
  }

  @Override
  public Optional<JwtInformation> findJwtInformationByAccessToken(String accessToken) {
    return findQueue(accessToken)
        .flatMap(queue -> queue.stream()
            .filter(info -> info.getAccessToken().equals(accessToken))
            .findFirst());
  }

  @Override
  public RotationResult rotateJwtInformation(String refreshToken, String newAccessToken,
      String newRefreshToken) {
    UUID userId = jwtTokenProvider.getUserId(refreshToken);
    if (userId == null) {
      return RotationResult.mismatch();
    }

    AtomicReference<RotationResult> result = new AtomicReference<>(RotationResult.mismatch());
    // 큐 순회와 교체를 같은 compute 안에 두어 사용자 단위로 직렬화한다
    origin.computeIfPresent(userId, (key, queue) -> {
      for (JwtInformation info : queue) {
        RotationResult attempt = info.rotateIfMatches(refreshToken, newAccessToken,
            newRefreshToken, refreshGraceWindow);
        if (attempt.isAccepted()) {
          result.set(attempt);
          log.debug("토큰 로테이션: userId={}, outcome={}", userId, attempt.outcome());
          break;
        }
      }
      return queue;
    });
    return result.get();
  }

  // 만료된 항목은 조회에 걸리지 않을 뿐 메모리에는 계속 남으므로 주기적으로 비운다
  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    int before = origin.values().stream().mapToInt(Queue::size).sum();
    // 리프레시 토큰까지 만료되면 재발급이 불가능하므로 보관할 이유가 없다.
    // compute 안에서 같은 맵을 다시 건드리면 안 되므로 여기서는 큐를 직접 정리한다.
    origin.values().forEach(
        queue -> queue.removeIf(info -> jwtTokenProvider.isExpired(info.getRefreshToken())));
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());

    int after = origin.values().stream().mapToInt(Queue::size).sum();
    if (before != after) {
      log.info("만료된 토큰 정보 정리: {}건 삭제", before - after);
    }
  }

  /** 토큰의 sub 클레임으로 소유자의 Queue만 찾는다. 전체 Map을 훑지 않기 위한 최적화. */
  private Optional<Queue<JwtInformation>> findQueue(String token) {
    UUID userId = jwtTokenProvider.getUserId(token);
    return userId == null ? Optional.empty() : Optional.ofNullable(origin.get(userId));
  }
}
