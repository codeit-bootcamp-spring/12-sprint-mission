package com.sprint.mission.discodeit.security;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;

  public InMemoryJwtRegistry(
      @Value("${discodeit.jwt.max-active-count:1}") int maxActiveJwtCount
  ) {
    if (maxActiveJwtCount < 1) {
      throw new IllegalArgumentException("maxActiveJwtCount must be at least 1");
    }
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    origin.compute(jwtInformation.getUserDto().id(), (userId, queue) -> {
      Queue<JwtInformation> active =
          queue == null ? new ConcurrentLinkedQueue<>() : queue;
      while (active.size() >= maxActiveJwtCount) {
        active.poll();
      }
      active.offer(jwtInformation);
      return active;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    origin.forEach((userId, queue) -> {
      queue.removeIf(information -> information.getRefreshToken().equals(refreshToken));
      if (queue.isEmpty()) {
        origin.remove(userId, queue);
      }
    });
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue != null && !queue.isEmpty();
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return containsToken(accessToken, true);
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return containsToken(refreshToken, false);
  }

  @Override
  public void rotateJwtInformation(
      String refreshToken,
      JwtInformation newJwtInformation
  ) {
    Queue<JwtInformation> queue = origin.get(newJwtInformation.getUserDto().id());
    if (queue == null) {
      throw new IllegalArgumentException("Inactive refresh token");
    }
    JwtInformation current = queue.stream()
        .filter(information -> information.getRefreshToken().equals(refreshToken))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Inactive refresh token"));
    current.rotate(
        newJwtInformation.getAccessToken(),
        newJwtInformation.getRefreshToken()
    );
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.forEach((userId, queue) -> {
      queue.removeIf(this::isExpired);
      if (queue.isEmpty()) {
        origin.remove(userId, queue);
      }
    });
  }

  private boolean containsToken(String token, boolean accessToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(information -> token.equals(
            accessToken ? information.getAccessToken() : information.getRefreshToken()
        ));
  }

  private boolean isExpired(JwtInformation information) {
    try {
      Date expiration = SignedJWT.parse(information.getRefreshToken())
          .getJWTClaimsSet()
          .getExpirationTime();
      return expiration == null || !expiration.after(new Date());
    } catch (ParseException exception) {
      return true;
    }
  }
}
