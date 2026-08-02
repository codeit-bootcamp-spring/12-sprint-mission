package com.sprint.mission.discodeit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionManager {

    private final SessionRegistry sessionRegistry;

    public List<SessionInformation> getActiveSessionByUserId(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof DiscodeitUserDetails)
                .map(DiscodeitUserDetails.class::cast)
                .filter(details -> userId.equals(details.getUserDto().id()))
                .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
                .toList();
    }

    public void invalidateSessionsByUserId(UUID userId) {
        List<SessionInformation> activeList = getActiveSessionByUserId(userId);
        if (!activeList.isEmpty()) {
            activeList.forEach(SessionInformation::expireNow);
        }
    }

    public boolean isOnline(UUID userId) {
        return !getActiveSessionByUserId(userId).isEmpty();
    }
}
