package com.sprint.mission.discodeit.dto.data.message;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record MessageResponse(
        UUID id,
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds,
        Instant createdAt,
        Instant updatedAt
) {
}