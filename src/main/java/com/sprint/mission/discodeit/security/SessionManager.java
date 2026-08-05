package com.sprint.mission.discodeit.security;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionManager {

  private final SessionRegistry sessionRegistry;

  public List<SessionInformation> getActiveSessionsByUserId(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(details -> details.getUserDto() != null)
        .filter(details -> userId.equals(details.getUserDto().id()))
        .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
        .toList();
  }

  public boolean isOnline(UUID userId) {
    return !getActiveSessionsByUserId(userId).isEmpty();
  }

  public void invalidateSessionsByUserId(UUID userId) {
    List<SessionInformation> activeSessions = getActiveSessionsByUserId(userId);

    activeSessions.forEach(SessionInformation::expireNow);

    if (!activeSessions.isEmpty()) {
      log.info("세션 무효화 완료: userId={}, sessionCount={}", userId, activeSessions.size());
    }
  }
}
