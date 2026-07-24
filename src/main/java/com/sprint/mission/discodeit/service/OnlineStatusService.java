package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import java.util.Set;
import java.util.UUID;

public interface OnlineStatusService {

    Set<UUID> getOnlineUserIds();
    boolean isOnline(UUID userId);
    boolean hasActiveSession(DiscodeitUserDetails principal);
}
