package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        UUID profileId,
        String username,
        String email,
        boolean isOnline,
        Instant createdAt,
        Instant updatedAt
) {
}