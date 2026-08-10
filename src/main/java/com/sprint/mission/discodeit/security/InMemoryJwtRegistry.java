package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

  private final int maxActiveJwtCount;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    origin.compute(jwtInformation.getUserDto().id(), (key, queue) -> {
      if (queue == null) {
        queue = new ConcurrentLinkedQueue<>();
      }

      if (queue.size() >= maxActiveJwtCount) {
        JwtInformation old = queue.poll();

        if (old != null) {
          refreshTokenIndexes.remove(old.getRefreshToken());
        }
      }

      queue.add(jwtInformation);
      refreshTokenIndexes.add(jwtInformation.getRefreshToken());

      return queue;
    });
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return refreshTokenIndexes.contains(refreshToken);
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    origin.computeIfPresent(newJwtInformation.getUserDto().id(), (key, queue) -> {
      queue.stream()
          .filter(jwt -> jwt.getRefreshToken().equals(refreshToken))
          .findFirst()
          .ifPresent(jwt -> {
            refreshTokenIndexes.remove(jwt.getRefreshToken());

            jwt.rotate(
                newJwtInformation.getAccessToken(),
                newJwtInformation.getRefreshToken()
            );

            refreshTokenIndexes.add(newJwtInformation.getRefreshToken());
          });
      return queue;
    });
  }
}
