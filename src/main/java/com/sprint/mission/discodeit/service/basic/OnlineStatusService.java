package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OnlineStatusService {

    private final SessionRegistry sessionRegistry;

    public Set<UUID> getOnlineUserIds() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(DiscodeitUserDetails.class::isInstance)
                .map(DiscodeitUserDetails.class::cast)
                .filter(this::hasActiveSession)
                .map(principal -> principal.getUserDto().id())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isOnline(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(DiscodeitUserDetails.class::isInstance)
                .map(DiscodeitUserDetails.class::cast)
                .filter(principal ->
                        principal.getUserDto().id().equals(userId)
                )
                .anyMatch(this::hasActiveSession);
    }

    private boolean hasActiveSession(DiscodeitUserDetails principal) {
        return !sessionRegistry
                .getAllSessions(principal, false)
                .isEmpty();
    }
}
