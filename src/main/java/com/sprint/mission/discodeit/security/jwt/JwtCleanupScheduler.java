package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtCleanupScheduler {

  private final JwtRegistry jwtRegistry;

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  public void clearExpiredJwtInformation() {
    jwtRegistry.clearExpiredJwtInformation();
  }
}