package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        UUID userId,
        Instant lastSeenAt,
        boolean online,
        Instant createdAt,
        Instant updatedAt
) {
}
