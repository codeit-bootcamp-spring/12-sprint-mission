package com.sprint.mission.discodeit.dto.data.userstatus;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserStatusResponse(
        UUID id,
        UUID userId,
        Instant lastActiveAt,
        boolean isOnline,
        Instant createdAt,
        Instant updatedAt
) {
}