package com.sprint.mission.discodeit.config;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;

  public InMemoryJwtRegistry(@Value("${jwt.max-active-count:1}") int maxActiveJwtCount) {
    if (maxActiveJwtCount < 1) {
      throw new IllegalArgumentException("최대 동시 로그인 수는 1 이상이어야 합니다.");
    }
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    origin.compute(jwtInformation.userResponse().id(), (userId, informations) -> {
      Queue<JwtInformation> queue =
          informations == null ? new ConcurrentLinkedQueue<>() : informations;

      // 같은 계정으로 로그인 요청이 동시에 들어오는 상황 대
      synchronized (queue) {
        removeExpired(queue);
        queue.offer(jwtInformation);
        while (queue.size() > maxActiveJwtCount) {
          queue.poll();
        }
      }

      return queue;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    origin.forEach((userId, queue) -> {
      synchronized (queue) {
        queue.removeIf(information -> information.refreshToken().equals(refreshToken));
        removeIfEmpty(userId, queue);
      }
    });
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return hasActiveInformation(userId, information -> true);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.entrySet().stream()
        .anyMatch(entry -> hasActiveInformation(entry.getKey(),
            information -> information.accessToken().equals(accessToken)));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.entrySet().stream()
        .anyMatch(entry -> hasActiveInformation(entry.getKey(),
            information -> information.refreshToken().equals(refreshToken)));
  }

  @Override
  public void rotateJwtInformation(String previousRefreshToken, JwtInformation jwtInformation) {
    UUID userId = jwtInformation.userResponse().id();
    Queue<JwtInformation> queue = origin.get(userId);

    if (queue == null) {
      return;
    }

    synchronized (queue) {
      boolean removed = queue.removeIf(info -> info.refreshToken().equals(previousRefreshToken));
      if (removed) {
        queue.offer(jwtInformation);
      }
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.forEach((userId, queue) -> {
      synchronized (queue) {
        removeExpired(queue);
        removeIfEmpty(userId, queue);
      }
    });
  }

  private boolean hasActiveInformation(
      UUID userId,
      Predicate<JwtInformation> predicate
  ) {
    Queue<JwtInformation> queue = origin.get(userId);

    if (queue == null) {
      return false;
    }

    synchronized (queue) {
      removeExpired(queue);
      boolean hasActive = queue.stream().anyMatch(predicate);
      removeIfEmpty(userId, queue);

      return hasActive;
    }
  }

  private void removeExpired(Queue<JwtInformation> queue) {
    Instant now = Instant.now();
    queue.removeIf(information -> !information.expiresAt().isAfter(now));
  }

  private void removeIfEmpty(UUID userId, Queue<JwtInformation> queue) {
    if (queue.isEmpty()) {
      origin.remove(userId, queue);
    }
  }
}
