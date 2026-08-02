package com.sprint.mission.discodeit.security;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

  private final SessionRegistry sessionRegistry;

  public List<SessionInformation> getActiveSessionByUserId(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(DiscodeitUserDetails.class::cast)
        .filter(details -> details.getUserDto() != null
            && userId.equals(details.getUserDto().id()))
        .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
        .toList();
  }

  public void invalidateSessionsByUserId(UUID userId) {
    List<SessionInformation> activeSessions = getActiveSessionByUserId(userId);

    if (!activeSessions.isEmpty()) {
      activeSessions.forEach(SessionInformation::expireNow);

      log.info("사용자 세션 무효화 완료: userId={}, sessionCount={}", userId, activeSessions.size());
    }
  }

  public boolean isOnline(UUID userId) {
    return !getActiveSessionByUserId(userId).isEmpty();
  }
}
