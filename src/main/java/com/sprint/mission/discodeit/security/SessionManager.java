package com.sprint.mission.discodeit.security;


import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

// 우리가 만들 세션 정책을 수행하는 매니저
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

  private final SessionRegistry sessionRegistry; // local 세션 저장소

  // userId를 기반으로 해서 활성화 되어 있는 모든 세션을 찾아오는 메소드
  public List<SessionInformation> getActiveSessionByUserId(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(DiscodeitUserDetails.class::cast)
        .filter(details -> details.getUserDto() != null &&
            userId.equals(details.getUserDto().id()))
        .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
        .toList();
  }


  // userId기반으로 모든 세션을 무효화 하는 메소드
  public void invalidateSessionsByUserId(UUID userId) {
    List<SessionInformation> activeList = getActiveSessionByUserId(userId);
    if (!activeList.isEmpty()) {
      activeList.forEach(SessionInformation::expireNow); // 세션 무효화 코드
      log.info("Sessions invalidate size : {}", activeList.size());
    }
  }

}
