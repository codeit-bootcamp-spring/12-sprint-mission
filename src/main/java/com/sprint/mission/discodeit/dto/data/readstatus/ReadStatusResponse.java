package com.sprint.mission.discodeit.dto.data.readstatus;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ReadStatusResponse(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadAt,
        Instant createdAt,
        Instant updatedAt
) {
}