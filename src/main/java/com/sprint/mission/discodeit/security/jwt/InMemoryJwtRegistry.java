package com.sprint.mission.discodeit.security.jwt;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 메모리에 JwtInformation을 보관하는 JwtRegistry 구현체.
 *
 * <p>서버 메모리에 두므로 인스턴스를 늘리면 로그인한 서버에서만 토큰이 유효해진다. 다중 인스턴스로
 * 확장하려면 Redis 같은 공유 저장소 기반 구현체로 교체해야 한다.
 */
@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  // <userId, Queue<JwtInformation>>
  // 토큰 문자열이 아니라 userId를 키로 두면 userId 단위 무효화와 동시 로그인 수 제어를 O(1)로 할 수 있다
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;
  private final JwtTokenProvider jwtTokenProvider;

  public InMemoryJwtRegistry(
      @Value("${discodeit.security.jwt.max-active-count}") int maxActiveJwtCount,
      JwtTokenProvider jwtTokenProvider
  ) {
    this.maxActiveJwtCount = maxActiveJwtCount;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.getUserDto().id();
    Queue<JwtInformation> queue =
        origin.computeIfAbsent(userId, key -> new ConcurrentLinkedQueue<>());
    queue.add(jwtInformation);

    // 최대 동시 로그인 수를 넘으면 가장 먼저 등록된 로그인부터 밀어낸다
    // (동일 계정으로 새로 로그인하면 기존 로그인이 무효화되는 동작)
    while (queue.size() > maxActiveJwtCount) {
      queue.poll();
      log.info("동시 로그인 수 초과로 기존 로그인 무효화: userId={}", userId);
    }
    log.debug("토큰 등록: userId={}, activeCount={}", userId, queue.size());
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
    findQueue(refreshToken).ifPresent(queue -> {
      boolean removed =
          queue.removeIf(info -> info.getRefreshToken().equals(refreshToken));
      if (removed) {
        log.info("리프레시 토큰으로 로그인 무효화: userId={}",
            jwtTokenProvider.getUserId(refreshToken));
      }
    });
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue == null) {
      return false;
    }
    // 리프레시 토큰은 2주를 살기 때문에, 온라인 판단은 엑세스 토큰이 살아있는지로 한다
    return queue.stream()
        .anyMatch(info -> !jwtTokenProvider.isExpired(info.getAccessToken()));
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return findQueue(accessToken)
        .map(queue -> queue.stream()
            .anyMatch(info -> info.getAccessToken().equals(accessToken)))
        .orElse(false);
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return findQueue(refreshToken)
        .map(queue -> queue.stream()
            .anyMatch(info -> info.getRefreshToken().equals(refreshToken)))
        .orElse(false);
  }

  @Override
  public Optional<JwtInformation> findJwtInformationByAccessToken(String accessToken) {
    return findQueue(accessToken)
        .flatMap(queue -> queue.stream()
            .filter(info -> info.getAccessToken().equals(accessToken))
            .findFirst());
  }

  @Override
  public Optional<JwtInformation> rotateJwtInformation(String refreshToken,
      JwtInformation newJwtInformation) {
    return findQueue(refreshToken)
        .flatMap(queue -> queue.stream()
            .filter(info -> info.getRefreshToken().equals(refreshToken))
            .findFirst())
        .map(info -> {
          // 제자리에서 교체하므로 이전 토큰 쌍은 이 시점부터 레지스트리에 존재하지 않게 된다.
          // 이후 같은 리프레시 토큰이 다시 들어오면 탈취 후 재사용으로 간주할 수 있다.
          info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
          log.debug("토큰 로테이션: userId={}", info.getUserDto().id());
          return info;
        });
  }

  @Override
  public void clearExpiredJwtInformation() {
    int before = origin.values().stream().mapToInt(Queue::size).sum();
    // 리프레시 토큰까지 만료되면 재발급이 불가능하므로 보관할 이유가 없다
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
